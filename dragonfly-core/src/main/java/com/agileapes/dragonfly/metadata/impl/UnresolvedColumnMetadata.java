package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.error.MetadataCollectionError;
import com.agileapes.dragonfly.error.UnresolvedColumnMetadataError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.ValueGenerationType;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/30, 3:19)
 */
public class UnresolvedColumnMetadata extends AbstractColumnMetadata {

    public UnresolvedColumnMetadata(String name, TableMetadata<?> table) {
        super(name, table);
    }

    @Override
    public int getType() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedColumnMetadataError());
    }

    @Override
    public String getPropertyName() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedColumnMetadataError());
    }

    @Override
    public Class<?> getPropertyType() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedColumnMetadataError());
    }

    @Override
    public ColumnMetadata getForeignReference() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedColumnMetadataError());
    }

    @Override
    public ValueGenerationType getGenerationType() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedColumnMetadataError());
    }

    @Override
    public String getValueGenerator() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedColumnMetadataError());
    }

    @Override
    public boolean isNullable() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedColumnMetadataError());
    }

    @Override
    public int getLength() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedColumnMetadataError());
    }

    @Override
    public int getPrecision() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedColumnMetadataError());
    }

    @Override
    public int getScale() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedColumnMetadataError());
    }

}
