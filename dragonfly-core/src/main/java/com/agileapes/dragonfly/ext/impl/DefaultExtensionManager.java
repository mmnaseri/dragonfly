package com.agileapes.dragonfly.ext.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.basics.api.impl.MirrorFilter;
import com.agileapes.dragonfly.annotations.Extension;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionInterceptor;
import com.agileapes.dragonfly.entity.impl.ImmutableEntityDefinition;
import com.agileapes.dragonfly.ext.ExtensionExpressionParser;
import com.agileapes.dragonfly.ext.ExtensionManager;
import com.agileapes.dragonfly.ext.ExtensionMetadata;
import com.agileapes.dragonfly.metadata.MetadataResolver;
import com.agileapes.dragonfly.metadata.ReferenceMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.TableMetadataInterceptor;
import com.agileapes.dragonfly.metadata.impl.ImmutableReferenceMetadata;
import com.agileapes.dragonfly.metadata.impl.ResolvedTableMetadata;
import com.agileapes.dragonfly.metadata.impl.TableMetadataCopier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/18, 16:37)
 */
public class DefaultExtensionManager implements ExtensionManager {

    private final Set<ExtensionMetadata> extensions = new CopyOnWriteArraySet<ExtensionMetadata>();
    private final ExtensionExpressionParser parser;
    private final MetadataResolver metadataResolver;

    public DefaultExtensionManager(MetadataResolver metadataResolver) {
        this(new DefaultExtensionExpressionParser(), metadataResolver);
    }

    public DefaultExtensionManager(ExtensionExpressionParser parser, MetadataResolver metadataResolver) {
        this.parser = parser;
        this.metadataResolver = metadataResolver;
    }

    @Override
    public void addExtension(final Class<?> extension) {
        final Extension annotation = extension.getAnnotation(Extension.class);
        final Filter<Class<?>> filter = parser.parse(annotation.filter());
        final TableMetadata<?> extendedTableMetadata = metadataResolver.resolve(extension);
        //noinspection unchecked
        final TableMetadataCopier<Object> tableMetadataCopier = new TableMetadataCopier<Object>((TableMetadata<Object>) extendedTableMetadata);
        extensions.add(new ImmutableExtensionMetadata(extension, new TableMetadataInterceptor() {
            @Override
            public <E> TableMetadata<E> intercept(final TableMetadata<E> tableMetadata) {
                if (extendedTableMetadata.getColumns().isEmpty()) {
                    return tableMetadata;
                }
                final TableMetadata<Object> metadata = tableMetadataCopier.copy();
                return new ResolvedTableMetadata<E>(tableMetadata.getEntityType(), tableMetadata.getSchema(), tableMetadata.getName(),
                        with(tableMetadata.getConstraints()).add(metadata.getConstraints()).list(),
                        with(tableMetadata.getColumns()).add(metadata.getColumns()).list(),
                        with(tableMetadata.getNamedQueries()).add(metadata.getNamedQueries()).list(),
                        with(tableMetadata.getSequences()).add(metadata.getSequences()).list(),
                        with(tableMetadata.getProcedures()).add(metadata.getProcedures()).list(),
                        with(tableMetadata.getForeignReferences()).add(with(metadata.getForeignReferences()).transform(new Transformer<ReferenceMetadata<Object, ?>, ReferenceMetadata<E, ?>>() {
                            @Override
                            public ReferenceMetadata<E, ?> map(ReferenceMetadata<Object, ?> input) {
                                //noinspection unchecked
                                return new ImmutableReferenceMetadata<E, Object>(input.getDeclaringClass(), input.getPropertyName(), input. isRelationOwner(), tableMetadata, (TableMetadata<Object>) input.getForeignTable(), input.getForeignColumn(), input.getRelationType(), input.getCascadeMetadata(), input.isLazy(), input.getOrdering());
                            }
                        }).list()).list(), tableMetadata.getVersionColumn(),
                        with(tableMetadata.getOrdering()).add(metadata.getOrdering()).list());
            }
        }, new EntityDefinitionInterceptor() {
            @Override
            public <E> EntityDefinition<E> intercept(EntityDefinition<E> definition) {
                final HashMap<Class<?>, Class<?>> interfaces = new HashMap<Class<?>, Class<?>>();
                interfaces.putAll(definition.getInterfaces());
                for (Class<?> superType : extension.getInterfaces()) {
                    interfaces.put(superType, extension);
                }
                return new ImmutableEntityDefinition<E>(definition.getEntityType(), interfaces);
            }
        }, filter));
    }

    @Override
    public Collection<TableMetadataInterceptor> getMetadataInterceptors(Class<?> entityType) {
        //noinspection unchecked
        return with(extensions).keep(new MirrorFilter<Class<?>>(entityType)).transform(new Transformer<ExtensionMetadata, TableMetadataInterceptor>() {
            @Override
            public TableMetadataInterceptor map(ExtensionMetadata input) {
                return input.getTableMetadataInterceptor();
            }
        }).list();
    }

    @Override
    public Collection<EntityDefinitionInterceptor> getDefinitionInterceptors(Class<?> entityType) {
        //noinspection unchecked
        return with(extensions).keep(new MirrorFilter<Class<?>>(entityType)).transform(new Transformer<ExtensionMetadata, EntityDefinitionInterceptor>() {
            @Override
            public EntityDefinitionInterceptor map(ExtensionMetadata input) {
                return input.getEntityDefinitionInterceptor();
            }
        }).list();
    }

    @Override
    public <E> EntityDefinition<E> intercept(EntityDefinition<E> definition) {
        EntityDefinition<E> entityDefinition = definition;
        final Collection<EntityDefinitionInterceptor> interceptors = getDefinitionInterceptors(entityDefinition.getEntityType());
        for (EntityDefinitionInterceptor interceptor : interceptors) {
            entityDefinition = interceptor.intercept(entityDefinition);
        }
        return entityDefinition;
    }

    @Override
    public <E> TableMetadata<E> intercept(TableMetadata<E> tableMetadata) {
        TableMetadata<E> metadata = tableMetadata;
        final Collection<TableMetadataInterceptor> interceptors = getMetadataInterceptors(metadata.getEntityType());
        for (TableMetadataInterceptor interceptor : interceptors) {
            metadata = interceptor.intercept(metadata);
        }
        return metadata;
    }

}
