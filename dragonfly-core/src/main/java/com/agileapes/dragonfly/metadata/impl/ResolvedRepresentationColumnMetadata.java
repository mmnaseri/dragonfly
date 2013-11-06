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
 * @since 1.0 (2013/10/3, 9:41)
 */
public class ResolvedRepresentationColumnMetadata extends ResolvedColumnMetadata {

    private final String actualProperty;

    public ResolvedRepresentationColumnMetadata(TableMetadata<?> table, Class<?> declaringClass, String name, int type, String propertyName, Class<?> propertyType, boolean nullable, int length, int precision, int scale, ValueGenerationType generationType, String valueGenerator, ColumnMetadata foreignReference, String actualProperty) {
        super(table, declaringClass, name, type, propertyName, propertyType, nullable, length, precision, scale, generationType, valueGenerator, foreignReference);
        this.actualProperty = actualProperty;
    }

    public String getActualProperty() {
        return actualProperty;
    }

}
