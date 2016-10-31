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

package com.mmnaseri.dragonfly.fluent.impl;

import com.mmnaseri.dragonfly.fluent.JoinQueryAddenda;
import com.mmnaseri.dragonfly.fluent.JoinQueryExpander;
import com.mmnaseri.dragonfly.fluent.generation.BookKeeper;
import com.mmnaseri.dragonfly.fluent.generation.ComparisonType;
import com.mmnaseri.dragonfly.fluent.generation.JoinType;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/10 AD, 12:42)
 */
public class DefaultJoinQueryExpander<G, E, F> implements JoinQueryExpander<E, F> {

    private final DefaultSelectQueryInitiator<E> initializer;
    private final JoinType joinType;
    private final BookKeeper<G> bookKeeper;
    private final F source;

    public DefaultJoinQueryExpander(DefaultSelectQueryInitiator<E> initializer, JoinType joinType, BookKeeper<G> bookKeeper, F source) {
        this.initializer = initializer;
        this.joinType = joinType;
        this.bookKeeper = bookKeeper;
        this.source = source;
    }

    private JoinQueryAddenda<E> addJoin(ComparisonType comparisonType, F target) {
        initializer.introduceJoin(joinType, bookKeeper, comparisonType, source, target);
        return initializer;
    }

    @Override
    public JoinQueryAddenda<E> isEqualTo(F target) {
        return addJoin(ComparisonType.IS_EQUAL_TO, target);
    }

    @Override
    public JoinQueryAddenda<E> isNotEqualTo(F value) {
        return addJoin(ComparisonType.IS_NOT_EQUAL_TO, value);
    }

    @Override
    public JoinQueryAddenda<E> isGreaterThan(F value) {
        return addJoin(ComparisonType.IS_GREATER_THAN, value);
    }

    @Override
    public JoinQueryAddenda<E> isLessThan(F value) {
        return addJoin(ComparisonType.IS_LESS_THAN, value);
    }

    @Override
    public JoinQueryAddenda<E> isGreaterThanOrEqualTo(F value) {
        return addJoin(ComparisonType.IS_GREATER_THAN_OR_EQUAL_TO, value);
    }

    @Override
    public JoinQueryAddenda<E> isLessThanOrEqualTo(F value) {
        return addJoin(ComparisonType.IS_LESS_THAN_OR_EQUAL_TO, value);
    }

}
