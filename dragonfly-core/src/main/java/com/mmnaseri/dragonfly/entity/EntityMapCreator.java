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

package com.mmnaseri.dragonfly.entity;

import com.mmnaseri.dragonfly.metadata.ColumnMetadata;
import com.mmnaseri.dragonfly.metadata.TableMetadata;

import java.util.Collection;
import java.util.Map;

/**
 * This interface will help with the process of converting entities into
 * maps from column name to property values.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/8/31, 17:13)
 */
public interface EntityMapCreator {

    /**
     * Converts an entity with the given table metadata into a map
     * @param tableMetadata    the table metadata
     * @param entity           the entity to be converted
     * @param <E>              the type of the entity
     * @return the map corresponding with the input entity
     */
    <E> Map<String, Object> toMap(TableMetadata<E> tableMetadata, E entity);

    /**
     * Converts an entity with the given column metadata into a map
     * @param columns          the column metadata for the entity's columns
     * @param entity           the entity to be converted
     * @param <E>              the type of the entity
     * @return the map corresponding with the input entity
     */
    <E> Map<String, Object> toMap(Collection<ColumnMetadata> columns, E entity);

}
