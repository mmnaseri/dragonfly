package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.maven.resource.ProjectClassResourceTransformer;
import com.agileapes.couteau.maven.resource.ProjectResource;
import com.agileapes.couteau.maven.resource.ProjectResourceType;
import com.agileapes.couteau.maven.resource.ProjectResourceTypeFilter;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.agileapes.dragonfly.annotations.Extension;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.entity.EntityDefinitionInterceptor;
import com.agileapes.dragonfly.entity.impl.ImmutableEntityDefinition;
import com.agileapes.dragonfly.ext.ExtensionResolver;
import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.metadata.impl.*;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import com.agileapes.dragonfly.tools.ColumnNameFilter;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 14:47)
 */
@Component("findExtensions")
public class DiscoverExtensionsTask extends PluginTask<PluginExecutor> {

    @Override
    protected String getIntro() {
        return "Discovering extensions ...";
    }

    private final Set<TableMetadataInterceptor> tableInterceptors = new CopyOnWriteArraySet<TableMetadataInterceptor>();
    @Autowired
    private EntityDefinitionContext definitionContext;

    @Override
    public void execute(PluginExecutor executor) throws MojoFailureException {
        final AnnotationMetadataResolver metadataResolver = new AnnotationMetadataResolver(false);
        final ExtensionResolver extensionResolver = new ExtensionResolver();
        final Collection<ProjectResource> projectResources = executor.getProjectResources();
        //noinspection unchecked
        with(projectResources)
        .keep(new ProjectResourceTypeFilter(ProjectResourceType.CLASS))
        .transform(new ProjectClassResourceTransformer())
        .keep(new AnnotatedElementFilter(Extension.class))
        .each(new Processor<Class<?>>() {
            @Override
            public void process(final Class<?> extension) {
                if (extension.getInterfaces().length == 0) {
                    return;
                }
                definitionContext.addInterceptor(new EntityDefinitionInterceptor() {
                    @Override
                    public <E> EntityDefinition<E> intercept(EntityDefinition<E> definition) {
                        final Map<Class<?>,Class<?>> interfaces = definition.getInterfaces();
                        for (Class<?> superType : extension.getInterfaces()) {
                            interfaces.put(superType, extension);
                        }
                        return new ImmutableEntityDefinition<E>(definition.getEntityType(), interfaces);
                    }
                });
            }
        })
        .transform(new Transformer<Class<?>, TableMetadata<?>>() {
            @Override
            public TableMetadata<?> map(Class<?> input) {
                return metadataResolver.resolve(input);
            }
        }).drop(new Filter<TableMetadata<?>>() {
            @Override
            public boolean accepts(TableMetadata<?> item) {
                return item.getColumns().isEmpty();
            }
        }).each(new Processor<TableMetadata<?>>() {
            @Override
            public void process(final TableMetadata<?> metadata) {
                final Filter<Class<?>> filter = extensionResolver.resolve(metadata.getEntityType().getAnnotation(Extension.class).descriptor());
                tableInterceptors.add(new TableMetadataInterceptor() {
                    @Override
                    public <E> TableMetadata<E> intercept(final TableMetadata<E> tableMetadata) {
                        if (!filter.accepts(tableMetadata.getEntityType())) {
                            return tableMetadata;
                        }
                        //noinspection unchecked
                        final TableMetadata<Object> extension = new TableMetadataCopier<Object>((TableMetadata<Object>) metadata).copy();
                        final List<ConstraintMetadata> constraints = with(tableMetadata.getConstraints()).add(extension.getConstraints()).list();
                        final List<ColumnMetadata> columns = with(tableMetadata.getColumns()).add(extension.getColumns()).list();
                        final List<NamedQueryMetadata> namedQueries = with(tableMetadata.getNamedQueries()).add(extension.getNamedQueries()).list();
                        final List<SequenceMetadata> sequences = with(tableMetadata.getSequences()).add(extension.getSequences()).list();
                        final List<StoredProcedureMetadata> storedProcedures = with(tableMetadata.getProcedures()).add(extension.getProcedures()).list();
                        final List<ReferenceMetadata<E, ?>> foreignReferences = with(tableMetadata.getForeignReferences()).add(with(extension.getForeignReferences()).transform(new Transformer<ReferenceMetadata<?, ?>, ReferenceMetadata<E, ?>>() {
                            @Override
                            public ReferenceMetadata<E, ?> map(ReferenceMetadata<?, ?> input) {
                                //noinspection unchecked
                                return new ImmutableReferenceMetadata<E, Object>(tableMetadata, input.getPropertyName(), (TableMetadata<Object>) input.getForeignTable(), input.getForeignColumn(), input.getRelationType(), input.getCascadeMetadata(), input.isLazy());
                            }
                        }).list()).list();
                        return new ResolvedTableMetadata<E>(tableMetadata.getEntityType(), tableMetadata.getSchema(), tableMetadata.getName(), constraints, columns, namedQueries, sequences, storedProcedures, foreignReferences);
                    }
                });
            }
        });
    }

    public Set<TableMetadataInterceptor> getTableInterceptors() {
        return tableInterceptors;
    }

    private class TableMetadataCopier<E> {

        private final TableMetadata<E> tableMetadata;

        private TableMetadataCopier(TableMetadata<E> tableMetadata) {
            this.tableMetadata = tableMetadata;
        }

        public TableMetadata<E> copy() {
            final List<ColumnMetadata> columns = with(tableMetadata.getColumns())
                    .transform(new Transformer<ColumnMetadata, ColumnMetadata>() {
                        @Override
                        public ColumnMetadata map(ColumnMetadata input) {
                            final ColumnMetadata foreignReference;
                            if (input.getForeignReference() == null) {
                                foreignReference = null;
                            } else {
                                //noinspection unchecked
                                foreignReference = new ResolvedColumnMetadata(new UnresolvedTableMetadata<Object>((Class<Object>) input.getForeignReference().getTable().getEntityType()), input.getForeignReference().getDeclaringClass(), input.getForeignReference().getName(), input.getForeignReference().getType(), input.getForeignReference().getPropertyName(), input.getForeignReference().getPropertyType(), input.getForeignReference().isNullable(), input.getForeignReference().getLength(), input.getForeignReference().getPrecision(), input.getForeignReference().getScale(), input.getForeignReference().getGenerationType(), input.getForeignReference().getValueGenerator(), null);
                            }
                            return new ResolvedColumnMetadata(null, input.getDeclaringClass(), input.getName(), input.getType(), input.getPropertyName(), input.getPropertyType(), input.isNullable(), input.getLength(), input.getPrecision(), input.getScale(), input.getGenerationType(), input.getValueGenerator(), foreignReference);
                        }
                    }).list();
            final ArrayList<ConstraintMetadata> constraints = new ArrayList<ConstraintMetadata>();
            final ArrayList<NamedQueryMetadata> namedQueries = new ArrayList<NamedQueryMetadata>();
            final ArrayList<SequenceMetadata> sequences = new ArrayList<SequenceMetadata>();
            final ArrayList<StoredProcedureMetadata> storedProcedures = new ArrayList<StoredProcedureMetadata>();
            final ArrayList<ReferenceMetadata<E, ?>> foreignReferences = new ArrayList<ReferenceMetadata<E, ?>>();
            final ResolvedTableMetadata<E> metadata = new ResolvedTableMetadata<E>(tableMetadata.getEntityType(), tableMetadata.getSchema(), tableMetadata.getName(), constraints, columns, namedQueries, sequences, storedProcedures, foreignReferences);
            final Transformer<ColumnMetadata, ColumnMetadata> columnTransformer = new Transformer<ColumnMetadata, ColumnMetadata>() {
                @Override
                public ColumnMetadata map(ColumnMetadata input) {
                    return with(columns).keep(new ColumnNameFilter(input.getName())).first();
                }
            };
            for (ConstraintMetadata constraintMetadata : tableMetadata.getConstraints()) {
                if (constraintMetadata instanceof PrimaryKeyConstraintMetadata) {
                    final PrimaryKeyConstraintMetadata constraint = (PrimaryKeyConstraintMetadata) constraintMetadata;
                    constraints.add(new PrimaryKeyConstraintMetadata(metadata, with(constraint.getColumns()).transform(columnTransformer).list()));
                } else if (constraintMetadata instanceof ForeignKeyConstraintMetadata) {
                    final ForeignKeyConstraintMetadata constraint = (ForeignKeyConstraintMetadata) constraintMetadata;
                    constraints.add(new ForeignKeyConstraintMetadata(metadata, with(constraint.getColumn()).transform(columnTransformer).first()));
                } else {
                    final UniqueConstraintMetadata constraint = (UniqueConstraintMetadata) constraintMetadata;
                    constraints.add(new UniqueConstraintMetadata(metadata, with(constraint.getColumns()).transform(columnTransformer).list()));
                }
            }
            for (NamedQueryMetadata queryMetadata : tableMetadata.getNamedQueries()) {
                namedQueries.add(new ImmutableNamedQueryMetadata(queryMetadata.getName(), queryMetadata.getQuery()));
            }
            for (SequenceMetadata sequence : tableMetadata.getSequences()) {
                sequences.add(new DefaultSequenceMetadata(sequence.getName(), sequence.getInitialValue(), sequence.getPrefetchSize()));
            }
            for (StoredProcedureMetadata procedureMetadata : tableMetadata.getProcedures()) {
                storedProcedures.add(new ImmutableStoredProcedureMetadata(procedureMetadata.getName(), procedureMetadata.getResultType(), procedureMetadata.getParameters()));
            }
            for (ReferenceMetadata<E, ?> foreignReference : tableMetadata.getForeignReferences()) {
                foreignReferences.add(new ImmutableReferenceMetadata<E, Object>(metadata, foreignReference.getPropertyName(), (TableMetadata<Object>) foreignReference.getForeignTable(), foreignReference.getForeignColumn(), foreignReference.getRelationType(), foreignReference.getCascadeMetadata(), foreignReference.isLazy()));
            }
            return metadata;
        }

    }

}
