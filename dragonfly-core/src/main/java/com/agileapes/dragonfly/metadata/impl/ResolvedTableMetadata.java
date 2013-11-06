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

import com.agileapes.dragonfly.error.MetadataCollectionError;
import com.agileapes.dragonfly.error.NoPrimaryKeyDefinedError;
import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.tools.ConstraintTypeFilter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:35)
 */
public class ResolvedTableMetadata<E> extends AbstractTableMetadata<E> {

    private final String name;
    private final String schema;
    private final Collection<ConstraintMetadata> constraints;
    private final Collection<ColumnMetadata> columns;
    private final Collection<SequenceMetadata> sequences;
    private final Collection<StoredProcedureMetadata> procedures;
    private final ColumnMetadata versionColumn;
    private final List<OrderMetadata> ordering;
    private PrimaryKeyConstraintMetadata primaryKey = null;
    private final Collection<NamedQueryMetadata> namedQueries;
    private final Collection<ReferenceMetadata<E, ?>> foreignReferences;

    public ResolvedTableMetadata(Class<E> entityType, String schema, String name, Collection<ConstraintMetadata> constraints, Collection<ColumnMetadata> columns, Collection<NamedQueryMetadata> namedQueries, Collection<SequenceMetadata> sequences, Collection<StoredProcedureMetadata> storedProcedures, Collection<ReferenceMetadata<E, ?>> foreignReferences, ColumnMetadata versionColumn, List<OrderMetadata> ordering) {
        super(entityType);
        this.schema = schema;
        this.name = name;
        this.constraints = constraints;
        this.columns = columns;
        this.namedQueries = namedQueries;
        this.sequences = sequences;
        this.procedures = storedProcedures;
        this.foreignReferences = foreignReferences;
        this.versionColumn = versionColumn;
        this.ordering = new DefaultResultOrderMetadata(ordering == null ? Collections.<OrderMetadata>emptyList() : ordering);
        for (ColumnMetadata column : columns) {
            if (column instanceof ResolvedColumnMetadata) {
                ResolvedColumnMetadata metadata = (ResolvedColumnMetadata) column;
                metadata.setTable(this);
            }
        }
        for (ConstraintMetadata constraint : constraints) {
            ((AbstractConstraintMetadata) constraint).setTable(this);
        }
        for (StoredProcedureMetadata procedure : procedures) {
            ((ImmutableStoredProcedureMetadata) procedure).setTable(this);
        }
        for (ReferenceMetadata<E, ?> foreignReference : this.foreignReferences) {
            //noinspection unchecked
            ((ImmutableReferenceMetadata<E, ?>) foreignReference).setLocalTable(this);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public Collection<ConstraintMetadata> getConstraints() {
        return Collections.unmodifiableCollection(constraints);
    }

    @Override
    public Collection<ColumnMetadata> getColumns() {
        return Collections.unmodifiableCollection(columns);
    }

    @Override
    public boolean hasPrimaryKey() {
        prepareKey();
        return primaryKey != null;
    }

    @Override
    public Collection<NamedQueryMetadata> getNamedQueries() {
        return namedQueries;
    }

    @Override
    public Collection<SequenceMetadata> getSequences() {
        return sequences;
    }

    @Override
    public Collection<StoredProcedureMetadata> getProcedures() {
        return procedures;
    }

    @Override
    public ColumnMetadata getVersionColumn() {
        return versionColumn;
    }

    @Override
    public List<OrderMetadata> getOrdering() {
        return ordering;
    }

    @Override
    public PrimaryKeyConstraintMetadata getPrimaryKey() {
        if (!hasPrimaryKey()) {
            throw new MetadataCollectionError("Cannot return table primary key", new NoPrimaryKeyDefinedError(getEntityType()));
        }
        return primaryKey;
    }

    private void prepareKey() {
        if (primaryKey == null) {
            primaryKey = (PrimaryKeyConstraintMetadata) with(constraints).keep(new ConstraintTypeFilter(PrimaryKeyConstraintMetadata.class)).first();
        }
    }

    @Override
    public <C extends ConstraintMetadata> Collection<C> getConstraints(Class<C> constraintType) {
        //noinspection unchecked
        return (Collection<C>) with(getConstraints()).keep(new ConstraintTypeFilter(constraintType)).list();
    }

    @Override
    public Collection<ReferenceMetadata<E, ?>> getForeignReferences() {
        return foreignReferences;
    }

    public void recreateForeignReferences() {

    }

}
