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

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/9 AD, 16:36)
 */
public interface CrossReferenceQueryAddenda<E> extends LimitQueryAddenda<E> {

    /**
     * Adds a UNION to the query
     * @param query    the query to which the union is being performed
     * @param <H>      the type of the main source for the target query
     * @param <G>      the type of the resulting binding for the query
     * @return the addenda for expanding the query
     */
    <H, G> CrossReferenceQueryAddenda<E> union(SelectQueryExecution<H, G> query);

    /**
     * Adds a UNION ALL to the query
     * @param query    the query to which the union is being performed
     * @param <H>      the type of the main source for the target query
     * @param <G>      the type of the resulting binding for the query
     * @return the addenda for expanding the query
     */
    <H, G> CrossReferenceQueryAddenda<E> unionAll(SelectQueryExecution<H, G> query);

    /**
     * Adds an INTERSECT to the query
     * @param query    the query to which the union is being performed
     * @param <H>      the type of the main source for the target query
     * @param <G>      the type of the resulting binding for the query
     * @return the addenda for expanding the query
     */
    <H, G> CrossReferenceQueryAddenda<E> intersect(SelectQueryExecution<H, G> query);

    /**
     * Adds an EXCEPT to the query
     * @param query    the query to which the union is being performed
     * @param <H>      the type of the main source for the target query
     * @param <G>      the type of the resulting binding for the query
     * @return the addenda for expanding the query
     */
    <H, G> CrossReferenceQueryAddenda<E> except(SelectQueryExecution<H, G> query);

}
