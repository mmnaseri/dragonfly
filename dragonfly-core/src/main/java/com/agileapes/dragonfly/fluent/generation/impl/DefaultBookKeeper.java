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

package com.agileapes.dragonfly.fluent.generation.impl;

import com.mmnaseri.couteau.reflection.beans.BeanWrapper;
import com.mmnaseri.couteau.reflection.beans.impl.MethodBeanWrapper;
import com.agileapes.dragonfly.fluent.error.AliasInitializationException;
import com.agileapes.dragonfly.fluent.generation.BookKeeper;
import com.agileapes.dragonfly.fluent.tools.QueryBuilderTools;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/10 AD, 11:26)
 */
public class DefaultBookKeeper<E> implements BookKeeper<E> {

    private final E entity;
    private final TableMetadata<E> tableMetadata;
    private final BeanWrapper<E> wrapper;
    private final Map<Object, ColumnMetadata> values;

    public DefaultBookKeeper(E entity, TableMetadata<E> tableMetadata) {
        this.entity = entity;
        this.tableMetadata = tableMetadata;
        this.wrapper = new MethodBeanWrapper<E>(entity);
        this.values = new HashMap<Object, ColumnMetadata>();
        handleColumns(tableMetadata.getColumns());
    }

    private void handleColumns(Collection<ColumnMetadata> columns) {
        for (ColumnMetadata column : columns) {
            final Object value = QueryBuilderTools.newObject(column.getPropertyType());
            try {
                final String propertyName = column.getPropertyName();
                if (!wrapper.hasProperty(propertyName) || !wrapper.isWritable(propertyName)) {
                    continue;
                }
                wrapper.setPropertyValue(propertyName, value);
            } catch (Exception e) {
                throw new AliasInitializationException("Failed to initialize alias of type " + wrapper.getBeanType().getCanonicalName(), e);
            }
            values.put(value, column);
        }
    }

    public ColumnMetadata getColumn(Object value) {
        for (Map.Entry<Object, ColumnMetadata> entry : values.entrySet()) {
            if (entry.getKey() == value) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public E getEntity() {
        return entity;
    }

    @Override
    public TableMetadata<E> getTable() {
        return tableMetadata;
    }

    @Override
    public Collection<Object> getValues() {
        return values.keySet();
    }

}
