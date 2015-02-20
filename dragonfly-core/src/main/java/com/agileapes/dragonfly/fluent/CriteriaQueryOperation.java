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

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/10 AD, 10:17)
 */
public interface CriteriaQueryOperation<E, F> {

    /**
     * Adds a {@code =} operation
     * @param value    the value to compare to
     * @return the addenda for expanding the query
     */
    CriteriaQueryExpander<E> isEqualTo(F value);

    /**
     * Adds a {@code !=} operation
     * @param value    the value to compare to
     * @return the addenda for expanding the query
     */
    CriteriaQueryExpander<E> isNotEqualTo(F value);

    /**
     * Adds a {@code >} operation
     * @param value    the value to compare to
     * @return the addenda for expanding the query
     */
    CriteriaQueryExpander<E> isGreaterThan(F value);

    /**
     * Adds a {@code <} operation
     * @param value    the value to compare to
     * @return the addenda for expanding the query
     */
    CriteriaQueryExpander<E> isLessThan(F value);

    /**
     * Adds a {@code >=} operation
     * @param value    the value to compare to
     * @return the addenda for expanding the query
     */
    CriteriaQueryExpander<E> isGreaterThanOrEqualTo(F value);

    /**
     * Adds a {@code <=} operation
     * @param value    the value to compare to
     * @return the addenda for expanding the query
     */
    CriteriaQueryExpander<E> isLessThanOrEqualTo(F value);

    /**
     * Adds a {@code LIKE} operation
     * @param value    the value to compare to
     * @param <G>      the type of the value
     * @return the addenda for expanding the query
     */
    <G extends CharSequence> CriteriaQueryExpander<E> isLike(G value);

    /**
     * Adds a {@code NOT LIKE} operation
     * @param value    the value to compare to
     * @param <G>      the type of the value
     * @return the addenda for expanding the query
     */
    <G extends CharSequence> CriteriaQueryExpander<E> isNotLike(G value);

    /**
     * Adds an {@code IS NULL} operation
     * @return the addenda for expanding the query
     */
    CriteriaQueryExpander<E> isNull();

    /**
     * Adds an {@code IS NOT NULL} operation
     * @return the addenda for expanding the query
     */
    CriteriaQueryExpander<E> isNotNull();

    /**
     * Adds an {@code IS IN} operation
     * @param query    the query object to look into
     * @param <G>      the type of the entity to query is locked into
     * @param <H>      the type of the binding provided to the query
     * @return the addenda for expanding the query
     */
    <G, H> CriteriaQueryExpander<E> isIn(SelectQueryExecution<G, H> query);

    /**
     * Adds an {@code IS NOT IN} operation
     * @param query    the query object to look into
     * @param <G>      the type of the entity to query is locked into
     * @param <H>      the type of the binding provided to the query
     * @return the addenda for expanding the query
     */
    <G, H> CriteriaQueryExpander<E> isNotIn(SelectQueryExecution<G, H> query);

}
