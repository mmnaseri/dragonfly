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

import com.agileapes.dragonfly.metadata.*;

import java.util.Collections;
import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 0:17)
 */
public class ImmutableRelationMetadata<S, D> implements RelationMetadata<S, D> {

    private final String propertyName;
    private final boolean owner;
    private final Class<?> declaringClass;
    private final List<OrderMetadata> ordering;
    private ColumnMetadata foreignColumn;
    private TableMetadata<S> localTable;
    private TableMetadata<D> foreignTable;
    private final RelationType relationType;
    private final boolean lazy;
    private final CascadeMetadata cascadeMetadata;

    public ImmutableRelationMetadata(Class<?> declaringClass, String propertyName, boolean owner, TableMetadata<S> localTable, TableMetadata<D> foreignTable, ColumnMetadata foreignColumn, RelationType relationType, CascadeMetadata cascadeMetadata, boolean lazy, List<OrderMetadata> ordering) {
        this.localTable = localTable;
        this.foreignTable = foreignTable;
        this.relationType = relationType;
        this.lazy = lazy;
        this.cascadeMetadata = cascadeMetadata;
        this.foreignColumn = foreignColumn;
        this.propertyName = propertyName;
        this.owner = owner;
        this.declaringClass = declaringClass;
        this.ordering = new DefaultResultOrderMetadata(ordering == null ? Collections.<OrderMetadata>emptyList() : ordering);
    }

    @Override
    public TableMetadata<S> getLocalTable() {
        return localTable;
    }

    @Override
    public TableMetadata<D> getForeignTable() {
        return foreignTable;
    }

    @Override
    public RelationType getType() {
        return relationType;
    }

    @Override
    public boolean isLazy() {
        return lazy;
    }

    @Override
    public CascadeMetadata getCascadeMetadata() {
        return cascadeMetadata;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public ColumnMetadata getForeignColumn() {
        return foreignColumn;
    }

    @Override
    public boolean isOwner() {
        return owner;
    }

    @Override
    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    @Override
    public List<OrderMetadata> getOrdering() {
        return ordering;
    }

    public void setLocalTable(TableMetadata<S> localTable) {
        this.localTable = localTable;
    }

    public void setForeignColumn(ColumnMetadata foreignColumn) {
        this.foreignColumn = foreignColumn;
        //noinspection unchecked
        this.foreignTable = (TableMetadata<D>) foreignColumn.getTable();
    }

}
