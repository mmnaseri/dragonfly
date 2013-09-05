package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.couteau.basics.api.Cache;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.api.impl.ConcurrentCache;
import com.agileapes.dragonfly.error.*;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.MetadataContext;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.tools.ColumnNameFilter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/30, 15:46)
 */
public class DefaultMetadataContext extends DefaultMetadataRegistry implements MetadataContext {

    private final Cache<Class<?>, TableMetadata<?>> metadataCache = new ConcurrentCache<Class<?>, TableMetadata<?>>();
    private final Set<MetadataRegistry> registries = new CopyOnWriteArraySet<MetadataRegistry>();
    private final Set<Class<?>> entityTypes = new HashSet<Class<?>>();
    private boolean ready = true;

    public DefaultMetadataContext() {
        addMetadataRegistry(this);
    }

    @Override
    public Collection<Class<?>> getEntityTypes() {
        return entityTypes;
    }

    @Override
    public void addMetadataRegistry(MetadataRegistry registry) {
        registries.add(registry);
        registry.setChangeCallback(new Processor<MetadataRegistry>() {
            @Override
            public void process(MetadataRegistry registry) {
                entityTypes.addAll(registry.getEntityTypes());
                ready = false;
            }
        });
    }

    @Override
    public <E> TableMetadata<E> getTableMetadata(final Class<E> entityType) {
        if (!ready) {
            rebuildCache();
        }
        //noinspection unchecked
        return (TableMetadata<E>) metadataCache.read(entityType);
    }

    private synchronized void rebuildCache() {
        //we first clear the cache
        metadataCache.invalidate();
        //then we fill a map from entity-type to table metadata from each registry
        final Map<Class<?>, TableMetadata<?>> map = new HashMap<Class<?>, TableMetadata<?>>();
        for (MetadataRegistry registry : registries) {
            final Collection<Class<?>> entityTypes = registry == this ? super.getEntityTypes() : registry.getEntityTypes();
            for (Class<?> entityType : entityTypes) {
                map.put(entityType, registry == this ? super.getTableMetadata(entityType) : registry.getTableMetadata(entityType));
            }
        }
        //we then resolve all unresolved foreign references
        for (Map.Entry<Class<?>, TableMetadata<?>> entry : map.entrySet()) {
            final TableMetadata<?> metadata = entry.getValue();
            for (ColumnMetadata columnMetadata : metadata.getColumns()) {
                if (columnMetadata instanceof UnresolvedColumnMetadata) {
                    throw new MetadataCollectionError("Metadata cannot be resolved", new UnresolvedColumnMetadataError());
                }
                final ColumnMetadata foreignColumn = columnMetadata.getForeignReference();
                if (foreignColumn == null) {
                    continue;
                }
                if (!(foreignColumn instanceof UnresolvedColumnMetadata)) {
                    continue;
                }
                TableMetadata<?> foreignTable = foreignColumn.getTable();
                final ColumnMetadata resolvedColumn;
                if (foreignTable instanceof UnresolvedTableMetadata<?>) {
                    foreignTable = map.get(foreignTable.getEntityType());
                }
                if (foreignTable == null || !(foreignTable instanceof ResolvedTableMetadata<?>)) {
                    throw new NoSuchEntityError(foreignColumn.getTable().getEntityType());
                }
                if (foreignColumn.getName() == null || foreignColumn.getName().isEmpty()) {
                    final PrimaryKeyConstraintMetadata primaryKey = foreignTable.getPrimaryKey();
                    if (primaryKey.getColumns().size() != 1) {
                        throw new EntityDefinitionError("Entity " + metadata.getEntityType().getCanonicalName() + " references composite primary key at " + foreignTable.getEntityType().getCanonicalName());
                    }
                    resolvedColumn = primaryKey.getColumns().iterator().next();
                } else {
                    resolvedColumn = with(foreignTable.getColumns()).keep(new ColumnNameFilter(foreignColumn.getName())).first();
                    if (resolvedColumn == null) {
                        throw new NoSuchColumnError(foreignTable.getEntityType(), foreignColumn.getName());
                    }
                }
                ((ResolvedColumnMetadata) columnMetadata).setForeignReference(resolvedColumn);
            }
        }
        for (Map.Entry<Class<?>, TableMetadata<?>> entry : map.entrySet()) {
            metadataCache.write(entry.getKey(), entry.getValue());
        }
    }

}
