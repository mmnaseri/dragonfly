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

package com.agileapes.dragonfly.fluent.impl;

import com.agileapes.dragonfly.fluent.HavingQueryExpander;
import com.agileapes.dragonfly.fluent.HavingQueryOperation;
import com.agileapes.dragonfly.fluent.generation.ComparisonType;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/10 AD, 15:35)
 */
public class DefaultHavingQueryOperation<E, F> implements HavingQueryOperation<E> {

    private final DefaultSelectQueryInitiator<E> initiator;
    private final F source;

    public DefaultHavingQueryOperation(DefaultSelectQueryInitiator<E> initiator, F source) {
        this.initiator = initiator;
        this.source = source;
    }

    private <G> HavingQueryExpander<E> compare(ComparisonType comparisonType, G value) {
        return initiator.introduceHavingComparison(comparisonType, source, value);
    }

    @Override
    public <G> HavingQueryExpander<E> beEqualTo(G value) {
        return compare(ComparisonType.IS_EQUAL_TO, value);
    }

    @Override
    public <G> HavingQueryExpander<E> notBeEqualTo(G value) {
        return compare(ComparisonType.IS_NOT_EQUAL_TO, value);
    }

    @Override
    public <G> HavingQueryExpander<E> beLessThan(G value) {
        return compare(ComparisonType.IS_LESS_THAN, value);
    }

    @Override
    public <G> HavingQueryExpander<E> beGreaterThan(G value) {
        return compare(ComparisonType.IS_GREATER_THAN, value);
    }

    @Override
    public <G> HavingQueryExpander<E> beLessThanOrEqualTo(G value) {
        return compare(ComparisonType.IS_LESS_THAN_OR_EQUAL_TO, value);
    }

    @Override
    public <G> HavingQueryExpander<E> beGreaterThanOrEqualTo(G value) {
        return compare(ComparisonType.IS_GREATER_THAN_OR_EQUAL_TO, value);
    }
}
