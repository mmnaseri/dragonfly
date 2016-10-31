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

package com.mmnaseri.dragonfly.metadata;

import java.util.List;

/**
 * Interface encapsulating the metadata for a reference between two tables
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/12, 0:10)
 */
public interface RelationMetadata<S, D> extends Metadata {

    /**
     * @return the current table
     */
    TableMetadata<S> getLocalTable();

    /**
     * @return the table this relation points to
     */
    TableMetadata<D> getForeignTable();

    /**
     * @return the type of the relation
     */
    RelationType getType();

    /**
     * @return whether fetch type for this relation is lazy
     */
    boolean isLazy();

    /**
     * @return the cascade metadata for this relation
     */
    CascadeMetadata getCascadeMetadata();

    /**
     * @return the name of the property defining the relation
     */
    String getPropertyName();

    /**
     * @return the column at the other side of the relation, on the foreign table
     */
    ColumnMetadata getForeignColumn();

    /**
     * @return {@code true} if the local table holds the key for the relation, which means
     * that there is a column on the local table that actually holds pointer values to the
     * rows of the foreign table.
     */
    boolean isOwner();

    /**
     * @return the class declaring the relation, which might or might not be the same as the
     * one for which the local table metadata has been declared, since relation metadata might
     * be inherited through Java classes or be defined by third party or extension classes.
     */
    Class<?> getDeclaringClass();

    /**
     * @return the ordering applied to the relation, when values are fetched for the local table
     */
    List<OrderMetadata> getOrdering();

}
