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
 * This metadata class is the same as {@link ResolvedColumnMetadata}, with the single difference
 * that it gives a chance to get the name of the actual property on the defining side of the
 * many-to-many relation to which it points.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/10/3, 9:41)
 */
public class ResolvedRepresentationColumnMetadata extends ResolvedColumnMetadata {

    private final String actualProperty;

    public ResolvedRepresentationColumnMetadata(TableMetadata<?> table, Class<?> declaringClass, String name, int type, String propertyName, Class<?> propertyType, boolean nullable, int length, int precision, int scale, ValueGenerationType generationType, String valueGenerator, ColumnMetadata foreignReference, String actualProperty, boolean collection) {
        super(table, declaringClass, name, type, propertyName, propertyType, nullable, length, precision, scale, generationType, valueGenerator, foreignReference, collection, false);
        this.actualProperty = actualProperty;
    }

    public String getActualProperty() {
        return actualProperty;
    }

}
