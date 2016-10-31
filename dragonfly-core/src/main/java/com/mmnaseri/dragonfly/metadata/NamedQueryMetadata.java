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

/**
 * This interface encapsulates the properties of a named query. It is assumed that all read
 * named queries will return entities of the type for which they are defined.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/7, 12:35)
 */
public interface NamedQueryMetadata extends Metadata {

    /**
     * @return the name of the query, which must be unique across the entity
     */
    String getName();

    /**
     * @return the actual query as specified by the developer. This might or might not be
     * suitable for immediate execution, i.e. it might require some static or dynamic
     * post-processing by the framework or third parties before it can be handed to the
     * persistence unit.
     */
    String getQuery();

    /**
     * @return the table for which the named query is defined.
     */
    TableMetadata<?> getTable();

    /**
     * @return the type of the query for execution
     */
    QueryType getQueryType();

}
