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

import com.mmnaseri.dragonfly.metadata.ColumnMetadata;
import com.mmnaseri.dragonfly.metadata.TableMetadata;
import com.mmnaseri.dragonfly.metadata.ValueGenerationType;

/**
 * This class holds metadata for a <em>resolved</em> column. This means that the column metadata
 * is fully accessible and available at this point.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/8/30, 3:14)
 */
public class ResolvedColumnMetadata extends AbstractColumnMetadata {

    private final int type;
    private final String propertyName;
    private final Class<?> propertyType;
    private final Class<?> declaringClass;
    private final boolean complex;
    private ColumnMetadata foreignReference;
    private final ValueGenerationType generationType;
    private final String valueGenerator;
    private final boolean collection;
    private final boolean nullable;
    private final int length;
    private final int precision;
    private final int scale;

    public ResolvedColumnMetadata(TableMetadata<?> table, Class<?> declaringClass, String name, int type, String propertyName, Class<?> propertyType, boolean nullable, int length, int precision, int scale, boolean collection, boolean complex) {
        this(table, declaringClass, name, type, propertyName, propertyType, nullable, length, precision, scale, null, null, collection, complex);
    }

    public ResolvedColumnMetadata(TableMetadata<?> table, Class<?> declaringClass, String name, int type, String propertyName, Class<?> propertyType, boolean nullable, int length, int precision, int scale, ValueGenerationType generationType, String valueGenerator, boolean collection, boolean complex) {
        this(table, declaringClass, name, type, propertyName, propertyType, nullable, length, precision, scale, generationType, valueGenerator, null, collection, complex);
    }

    public ResolvedColumnMetadata(TableMetadata<?> table, Class<?> declaringClass, String name, int type, String propertyName, Class<?> propertyType, boolean nullable, int length, int precision, int scale, ColumnMetadata foreignReference, boolean collection, boolean complex) {
        this(table, declaringClass, name, type, propertyName, propertyType, nullable, length, precision, scale, null, null, foreignReference, collection, complex);
    }

    public ResolvedColumnMetadata(TableMetadata<?> table, Class<?> declaringClass, String name, int type, String propertyName, Class<?> propertyType, boolean nullable, int length, int precision, int scale, ValueGenerationType generationType, String valueGenerator, ColumnMetadata foreignReference, boolean collection, boolean complex) {
        super(name, table);
        this.type = type;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.nullable = nullable;
        this.length = length;
        this.precision = precision;
        this.scale = scale;
        this.foreignReference = foreignReference;
        this.generationType = generationType;
        this.valueGenerator = valueGenerator;
        this.declaringClass = declaringClass;
        this.collection = collection;
        this.complex = complex;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public Class<?> getPropertyType() {
        return propertyType;
    }

    @Override
    public ColumnMetadata getForeignReference() {
        return foreignReference;
    }

    @Override
    public ValueGenerationType getGenerationType() {
        return generationType;
    }

    @Override
    public String getValueGenerator() {
        return valueGenerator;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public boolean isCollection() {
        return collection;
    }

    @Override
    public boolean isComplex() {
        return complex;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int getPrecision() {
        return precision;
    }

    @Override
    public int getScale() {
        return scale;
    }

    @Override
    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public void setTable(ResolvedTableMetadata<?> table) {
        super.setTable(table);
    }

    public void setForeignReference(ColumnMetadata foreignReference) {
        this.foreignReference = foreignReference;
        ((ResolvedTableMetadata) getTable()).recreateForeignReferences();
    }

}
