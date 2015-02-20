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

import com.agileapes.dragonfly.fluent.CriteriaQueryExpander;
import com.agileapes.dragonfly.fluent.CriteriaQueryOperation;
import com.agileapes.dragonfly.fluent.SelectQueryExecution;
import com.agileapes.dragonfly.fluent.error.MissingOperandException;
import com.agileapes.dragonfly.fluent.generation.ComparisonType;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/10 AD, 16:18)
 */
public class DefaultCriteriaQueryOperation<E, F> implements CriteriaQueryOperation<E, F> {

    private final DefaultSelectQueryInitiator<E> initiator;
    private final F source;

    public DefaultCriteriaQueryOperation(DefaultSelectQueryInitiator<E> initiator, F source) {
        this.initiator = initiator;
        this.source = source;
    }

    private <G> CriteriaQueryExpander<E> compare(ComparisonType comparisonType, G value) {
        if (value == null && !(comparisonType.equals(ComparisonType.IS_NULL) || comparisonType.equals(ComparisonType.IS_NOT_NULL))) {
            throw new MissingOperandException("Expected to find a comparison operand but found null: " + comparisonType.getOperator());
        }
        return initiator.introduceCriteriaComparison(comparisonType, source, value);
    }

    @Override
    public CriteriaQueryExpander<E> isEqualTo(F value) {
        return compare(ComparisonType.IS_EQUAL_TO, value);
    }

    @Override
    public CriteriaQueryExpander<E> isNotEqualTo(F value) {
        return compare(ComparisonType.IS_NOT_EQUAL_TO, value);
    }

    @Override
    public CriteriaQueryExpander<E> isGreaterThan(F value) {
        return compare(ComparisonType.IS_GREATER_THAN, value);
    }

    @Override
    public CriteriaQueryExpander<E> isLessThan(F value) {
        return compare(ComparisonType.IS_LESS_THAN, value);
    }

    @Override
    public CriteriaQueryExpander<E> isGreaterThanOrEqualTo(F value) {
        return compare(ComparisonType.IS_GREATER_THAN_OR_EQUAL_TO, value);
    }

    @Override
    public CriteriaQueryExpander<E> isLessThanOrEqualTo(F value) {
        return compare(ComparisonType.IS_LESS_THAN_OR_EQUAL_TO, value);
    }

    @Override
    public <G extends CharSequence> CriteriaQueryExpander<E> isLike(G value) {
        return compare(ComparisonType.IS_LIKE, value);
    }

    @Override
    public <G extends CharSequence> CriteriaQueryExpander<E> isNotLike(G value) {
        return compare(ComparisonType.IS_NOT_LIKE, value);
    }

    @Override
    public CriteriaQueryExpander<E> isNull() {
        return compare(ComparisonType.IS_NULL, null);
    }

    @Override
    public CriteriaQueryExpander<E> isNotNull() {
        return compare(ComparisonType.IS_NOT_NULL, null);
    }

    @Override
    public <G, H> CriteriaQueryExpander<E> isIn(SelectQueryExecution<G, H> query) {
        return compare(ComparisonType.IS_IN, query);
    }

    @Override
    public <G, H> CriteriaQueryExpander<E> isNotIn(SelectQueryExecution<G, H> query) {
        return compare(ComparisonType.IS_NOT_IN, query);
    }
}
