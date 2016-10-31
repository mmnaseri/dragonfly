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

package com.mmnaseri.dragonfly.fluent;

import com.mmnaseri.dragonfly.fluent.generation.JoinedSelectionSource;
import com.mmnaseri.dragonfly.fluent.generation.ParameterDescriptor;
import com.mmnaseri.dragonfly.fluent.generation.SelectionSource;
import com.mmnaseri.dragonfly.metadata.ColumnMetadata;

import java.util.List;
import java.util.Map;

/**
 * This interface represents a SELECT query that has been prepared for execution, but has not been executed yet.
 *
 * As such, this interface carries with itself all the state that is required for the query to run properly.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/9 AD, 16:40)
 */
public interface SelectQueryExecution<E, H> {

    /**
     * @return the type of the object being bound
     */
    Class<? extends H> getBindingType();

    /**
     * @return the object specifying the bindings between the result set and the properties of the returned objects
     */
    H getBinding();

    /**
     * @return the SQL statement for the SELECT query
     */
    String getSql();

    /**
     * @return the parameters for the call
     */
    List<ParameterDescriptor> getParameters();

    /**
     * @return the main source for the SELECT query
     */
    SelectionSource<E> getMainSource();

    /**
     * @return all the sources that have been JOINed with the main source
     */
    List<JoinedSelectionSource<?, ?>> getJoinedSources();

    /**
     * @return all the sources (effective union of main source and join sources)
     */
    List<SelectionSource<?>> getSources();

    /**
     * @return a map of alias objects to column alias labels as recognized by the database
     */
    Map<Object, String> getColumnAliases();

    /**
     * @return a map of alias objects to table aliases as in the SELECT query's FROM clause
     */
    Map<Object, String> getTableAliases();

    /**
     * @return a map of alias objects to column definitions for all the columns
     */
    Map<Object, ColumnMetadata> getColumns();
}
