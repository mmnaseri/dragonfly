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

package com.agileapes.dragonfly.fluent;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/10 AD, 10:31)
 */
public interface SelectQueryFinalizer<E> {

    /**
     * Performs the select query and returns results that are instances of the provided binding
     * @param binding    the binding object. Can be a function invocation, a list, a map, or an object.
     *                   If it is {@code null} then the main source will be chosen as the binding
     * @param <H>        the type of the binding
     * @return the result of the query
     */
    <H> List<? extends H> select(H binding);

    /**
     * Performs the select query and returns results that are instances of the provided binding. This is
     * the same as {@link #select(Object) select(null)}
     * @return the result of the query
     */
    List<? extends E> select();

    /**
     * Same as {@link #select(Object)} only it is a DISTINCT selection
     * @param binding    the binding object
     * @param <H>        the type of the binding
     * @return the result of the query
     */
    <H> List<? extends H> selectDistinct(H binding);

    /**
     * Same as {@link #select(Object)} only it is a DISTINCT selection with a {@code null} binding
     * @return the result of the query
     */
    List<? extends E> selectDistinct();

    /**
     * @param binding    the binding
     * @param <H>        the type of the binding
     * @return the selection state for the query
     */
    <H> SelectQueryExecution<E, H> selection(H binding);

    /**
     * @return the selection state for the query
     */
    SelectQueryExecution<E, E> selection();

    /**
     * @param binding    the binding
     * @param <H>        the type of the binding
     * @return the selection state for the query
     */
    <H> SelectQueryExecution<E, H> distinctSelection(H binding);

    /**
     * @return the selection state for the query
     */
    SelectQueryExecution<E, E> distinctSelection();


}
