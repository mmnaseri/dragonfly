/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
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

package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.error.MetadataCollectionError;
import com.agileapes.dragonfly.error.UnresolvedColumnMetadataError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.ValueGenerationType;

/**
 * This class will denote unresolved columns, wherein only the name and the table to which this
 * column belongs have been determined and are thus accessible.
 *
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
    public boolean isCollection() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedColumnMetadataError());
    }

    @Override
    public boolean isComplex() {
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

    @Override
    public Class<?> getDeclaringClass() {
        throw new MetadataCollectionError("Metadata is not available", new UnresolvedColumnMetadataError());
    }

}
