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
import com.agileapes.dragonfly.error.UnresolvedTableMetadataError;
import com.agileapes.dragonfly.metadata.*;

import java.util.Collection;
import java.util.List;

/**
 * This class denotes an unresolved table metadata, in whose case only the type of the entity for
 * which the table metadata has been defined is clearly available.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:46)
 */
public class UnresolvedTableMetadata<E> extends AbstractTableMetadata<E> {

    public UnresolvedTableMetadata(Class<E> entityType) {
        super(entityType);
    }

    @Override
    public String getName() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError(getEntityType()));
    }

    @Override
    public String getSchema() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError(getEntityType()));
    }

    @Override
    public Collection<ConstraintMetadata> getConstraints() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError(getEntityType()));
    }

    @Override
    public Collection<ColumnMetadata> getColumns() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError(getEntityType()));
    }

    @Override
    public boolean hasPrimaryKey() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError(getEntityType()));
    }

    @Override
    public Collection<NamedQueryMetadata> getNamedQueries() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError(getEntityType()));
    }

    @Override
    public Collection<SequenceMetadata> getSequences() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError(getEntityType()));
    }

    @Override
    public Collection<StoredProcedureMetadata> getProcedures() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError(getEntityType()));
    }

    @Override
    public ColumnMetadata getVersionColumn() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError(getEntityType()));
    }

    @Override
    public List<OrderMetadata> getOrdering() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError(getEntityType()));
    }

    @Override
    public PrimaryKeyConstraintMetadata getPrimaryKey() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError(getEntityType()));
    }

    @Override
    public Collection<RelationMetadata<E, ?>> getForeignReferences() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError(getEntityType()));
    }

    @Override
    public <C extends ConstraintMetadata> Collection<C> getConstraints(Class<C> constraintType) {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError(getEntityType()));
    }

}
