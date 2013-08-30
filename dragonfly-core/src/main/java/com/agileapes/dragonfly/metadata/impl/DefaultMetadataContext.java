package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.dragonfly.error.AmbiguousEntityError;
import com.agileapes.dragonfly.error.NoSuchEntityError;
import com.agileapes.dragonfly.metadata.MetadataContext;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/30, 15:46)
 */
public class DefaultMetadataContext extends DefaultMetadataRegistry implements MetadataContext {

    private final Set<MetadataRegistry> registries = new CopyOnWriteArraySet<MetadataRegistry>();

    public DefaultMetadataContext() {
        addMetadataRegistry(this);
    }

    @Override
    public void addMetadataRegistry(MetadataRegistry registry) {
        registries.add(registry);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> TableMetadata<E> getTableMetadata(final Class<E> entityType) {
        final List<TableMetadata<?>> list = with(registries).keep(new Filter<MetadataRegistry>() {
            @Override
            public boolean accepts(MetadataRegistry registry) {
                return registry.contains(entityType);
            }
        }).transform(new Transformer<MetadataRegistry, TableMetadata<?>>() {
            @Override
            public TableMetadata<?> map(MetadataRegistry registry) {
                return registry.getTableMetadata(entityType);
            }
        }).keep(new Filter<TableMetadata<?>>() {
            @Override
            public boolean accepts(TableMetadata<?> metadata) {
                return metadata.getEntityType().isAssignableFrom(entityType);
            }
        }).list();
        if (list.isEmpty()) {
            throw new NoSuchEntityError(entityType);
        }
        if (list.size() > 1) {
            throw new AmbiguousEntityError(entityType);
        }
        return (TableMetadata<E>) list.get(0);
    }

}
