package com.agileapes.dragonfly.ext.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.dragonfly.annotations.Extension;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionInterceptor;
import com.agileapes.dragonfly.entity.impl.ImmutableEntityDefinition;
import com.agileapes.dragonfly.ext.ExtensionExpressionParser;
import com.agileapes.dragonfly.ext.ExtensionMetadata;
import com.agileapes.dragonfly.ext.ExtensionMetadataResolver;
import com.agileapes.dragonfly.metadata.RelationMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.TableMetadataInterceptor;
import com.agileapes.dragonfly.metadata.TableMetadataResolver;
import com.agileapes.dragonfly.metadata.impl.ImmutableRelationMetadata;
import com.agileapes.dragonfly.metadata.impl.ResolvedTableMetadata;
import com.agileapes.dragonfly.metadata.impl.TableMetadataCopier;

import java.util.HashMap;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * This resolver will accept classes marked with {@link Extension}. After that, all interfaces
 * implemented by the class will be added to the definition of the entities targeted by the
 * extension, and all table-related metadata will be resolved through the class the same as the
 * way it was deduced from normal entities, and will afterwards be used to augment the table
 * metadata for all matching entity tables.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/11/11, 19:32)
 */
public class AnnotationExtensionMetadataResolver implements ExtensionMetadataResolver<Class<?>> {

    private final ExtensionExpressionParser parser;
    private final TableMetadataResolver tableMetadataResolver;

    public AnnotationExtensionMetadataResolver(TableMetadataResolver tableMetadataResolver) {
        this.parser = new DefaultExtensionExpressionParser();
        this.tableMetadataResolver = tableMetadataResolver;
    }

    @Override
    public ExtensionMetadata resolve(final Class<?> extension) {
        final Extension annotation = extension.getAnnotation(Extension.class);
        final Filter<Class<?>> filter = parser.map(annotation.filter());
        final TableMetadata<?> extendedTableMetadata = tableMetadataResolver.resolve(extension);
        //noinspection unchecked
        final TableMetadataCopier<Object> tableMetadataCopier = new TableMetadataCopier<Object>((TableMetadata<Object>) extendedTableMetadata);
        return new ImmutableExtensionMetadata(extension, new TableMetadataInterceptor() {
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
                        with(tableMetadata.getForeignReferences()).add(with(metadata.getForeignReferences()).transform(new Transformer<RelationMetadata<Object, ?>, RelationMetadata<E, ?>>() {
                            @Override
                            public RelationMetadata<E, ?> map(RelationMetadata<Object, ?> input) {
                                //noinspection unchecked
                                return new ImmutableRelationMetadata<E, Object>(input.getDeclaringClass(), input.getPropertyName(), input.isOwner(), tableMetadata, (TableMetadata<Object>) input.getForeignTable(), input.getForeignColumn(), input.getType(), input.getCascadeMetadata(), input.isLazy(), input.getOrdering());
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
        }, filter);
    }

    @Override
    public boolean accepts(Class<?> item) {
        return item.isAnnotationPresent(Extension.class);
    }

}
