/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.tools.ColumnNameFilter;

import java.util.ArrayList;
import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * This class will help with copying table metadata into a new instance.
 *
 * @param <E>    the type of the entity for which the table is defined and copied.
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:35)
 */
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
        final ArrayList<RelationMetadata<E, ?>> foreignReferences = new ArrayList<RelationMetadata<E, ?>>();
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
            namedQueries.add(new ImmutableNamedQueryMetadata(queryMetadata.getName(), queryMetadata.getQuery(), metadata, queryMetadata.getQueryType()));
        }
        for (SequenceMetadata sequence : tableMetadata.getSequences()) {
            sequences.add(new ImmutableSequenceMetadata(sequence.getName(), sequence.getInitialValue(), sequence.getPrefetchSize()));
        }
        for (StoredProcedureMetadata procedureMetadata : tableMetadata.getProcedures()) {
            storedProcedures.add(new DefaultStoredProcedureMetadata(procedureMetadata.getName(), procedureMetadata.getResultType(), procedureMetadata.getParameters()));
        }
        for (RelationMetadata<E, ?> foreignReference : tableMetadata.getForeignReferences()) {
            //noinspection unchecked
            foreignReferences.add(new DefaultRelationMetadata<E, Object>(foreignReference.getDeclaringClass(), foreignReference.getPropertyName(), foreignReference.isOwner(), metadata, (TableMetadata<Object>) foreignReference.getForeignTable(), foreignReference.getForeignColumn(), foreignReference.getType(), foreignReference.getCascadeMetadata(), foreignReference.isLazy(), foreignReference.getOrdering()));
        }
        return metadata;
    }

}
