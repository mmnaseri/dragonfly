/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mmnaseri.dragonfly.metadata.impl;

import com.mmnaseri.dragonfly.error.NoPrimaryKeyDefinedError;
import com.mmnaseri.dragonfly.metadata.*;
import com.mmnaseri.dragonfly.tools.ConstraintTypeFilter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;


/**
 * This class holds <em>resolved</em> table metadata, meaning that at this point, all metadata
 * regarding the table has been resolved and can be accessed, including its foreign references.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
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
    private final Collection<RelationMetadata<E, ?>> foreignReferences;

    public ResolvedTableMetadata(Class<E> entityType, String schema, String name, Collection<ConstraintMetadata> constraints, Collection<ColumnMetadata> columns, Collection<NamedQueryMetadata> namedQueries, Collection<SequenceMetadata> sequences, Collection<StoredProcedureMetadata> storedProcedures, Collection<RelationMetadata<E, ?>> foreignReferences, ColumnMetadata versionColumn, List<OrderMetadata> ordering) {
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
            ((DefaultStoredProcedureMetadata) procedure).setTable(this);
        }
        for (RelationMetadata<E, ?> foreignReference : this.foreignReferences) {
            //noinspection unchecked
            ((DefaultRelationMetadata<E, ?>) foreignReference).setLocalTable(this);
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
            throw new NoPrimaryKeyDefinedError(getEntityType());
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
    public Collection<RelationMetadata<E, ?>> getForeignReferences() {
        return foreignReferences;
    }

    public void recreateForeignReferences() {

    }

}
