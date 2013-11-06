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

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.ValueGenerationType;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/30, 3:14)
 */
public class ResolvedColumnMetadata extends AbstractColumnMetadata {

    private final int type;
    private final String propertyName;
    private final Class<?> propertyType;
    private final Class<?> declaringClass;
    private ColumnMetadata foreignReference;
    private final ValueGenerationType generationType;
    private final String valueGenerator;
    private final boolean nullable;
    private final int length;
    private final int precision;
    private final int scale;

    public ResolvedColumnMetadata(TableMetadata<?> table, Class<?> declaringClass, String name, int type, String propertyName, Class<?> propertyType, boolean nullable, int length, int precision, int scale) {
        this(table, declaringClass, name, type, propertyName, propertyType, nullable, length, precision, scale, null, null);
    }

    public ResolvedColumnMetadata(TableMetadata<?> table, Class<?> declaringClass, String name, int type, String propertyName, Class<?> propertyType, boolean nullable, int length, int precision, int scale, ValueGenerationType generationType, String valueGenerator) {
        this(table, declaringClass, name, type, propertyName, propertyType, nullable, length, precision, scale, generationType, valueGenerator, null);
    }

    public ResolvedColumnMetadata(TableMetadata<?> table, Class<?> declaringClass, String name, int type, String propertyName, Class<?> propertyType, boolean nullable, int length, int precision, int scale, ColumnMetadata foreignReference) {
        this(table, declaringClass, name, type, propertyName, propertyType, nullable, length, precision, scale, null, null, foreignReference);
    }

    public ResolvedColumnMetadata(TableMetadata<?> table, Class<?> declaringClass, String name, int type, String propertyName, Class<?> propertyType, boolean nullable, int length, int precision, int scale, ValueGenerationType generationType, String valueGenerator, ColumnMetadata foreignReference) {
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
