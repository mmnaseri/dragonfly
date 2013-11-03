package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.tools.ColumnNameFilter;

import java.util.ArrayList;
import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

public class TableMetadataCopier<E> {

    private final TableMetadata<E> tableMetadata;

    public TableMetadataCopier(TableMetadata<E> tableMetadata) {
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
        final ColumnMetadata versionColumn = tableMetadata.getVersionColumn() == null ? null : with(columns).find(new ColumnNameFilter(tableMetadata.getVersionColumn().getName()));
        final List<OrderMetadata> ordering = with(tableMetadata.getOrdering()).transform(new Transformer<OrderMetadata, OrderMetadata>() {
            @Override
            public OrderMetadata map(OrderMetadata input) {
                return new DefaultOrderMetadata(with(columns).find(new ColumnNameFilter(input.getColumn().getName())), input.getOrder());
            }
        }).list();
        final ResolvedTableMetadata<E> metadata = new ResolvedTableMetadata<E>(tableMetadata.getEntityType(), tableMetadata.getSchema(), tableMetadata.getName(), constraints, columns, namedQueries, sequences, storedProcedures, foreignReferences, versionColumn, ordering);
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
            //noinspection unchecked
            foreignReferences.add(new ImmutableReferenceMetadata<E, Object>(foreignReference.getDeclaringClass(), foreignReference.getPropertyName(), foreignReference.isRelationOwner(), metadata, (TableMetadata<Object>) foreignReference.getForeignTable(), foreignReference.getForeignColumn(), foreignReference.getRelationType(), foreignReference.getCascadeMetadata(), foreignReference.isLazy(), foreignReference.getOrdering()));
        }
        return metadata;
    }

}
