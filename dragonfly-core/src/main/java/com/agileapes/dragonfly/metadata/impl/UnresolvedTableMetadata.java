package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.error.MetadataCollectionError;
import com.agileapes.dragonfly.error.UnresolvedTableMetadataError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.ConstraintMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:46)
 */
public class UnresolvedTableMetadata<E> extends AbstractTableMetadata<E> {

    public UnresolvedTableMetadata(Class<E> entityType) {
        super(entityType);
    }

    @Override
    public String getName() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError());
    }

    @Override
    public String getSchema() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError());
    }

    @Override
    public String getQualifiedName() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError());
    }

    @Override
    public Collection<ConstraintMetadata> getConstraints() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError());
    }

    @Override
    public Collection<ColumnMetadata> getColumns() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError());
    }

    @Override
    public PrimaryKeyConstraintMetadata getPrimaryKey() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError());
    }

    @Override
    public Collection<TableMetadata<?>> getForeignReferences() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError());
    }

    @Override
    public <C extends ConstraintMetadata> Collection<C> getConstraints(Class<C> constraintType) {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedTableMetadataError());
    }

}
