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

import com.agileapes.dragonfly.annotations.Ordering;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.fluent.*;
import com.agileapes.dragonfly.fluent.error.ComparisonWithoutReferenceException;
import com.agileapes.dragonfly.fluent.error.CriteriaWithFunctionException;
import com.agileapes.dragonfly.fluent.error.HavingWithoutFunctionException;
import com.agileapes.dragonfly.fluent.error.ReferenceExpectedException;
import com.agileapes.dragonfly.fluent.generation.*;
import com.agileapes.dragonfly.fluent.generation.impl.DefaultBookKeeper;
import com.agileapes.dragonfly.fluent.generation.impl.ImmutableJoinedSelectionSource;
import com.agileapes.dragonfly.fluent.generation.impl.ImmutableOrder;
import com.agileapes.dragonfly.fluent.generation.impl.ImmutableToken;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/10 AD, 11:03)
 */
public class DefaultSelectQueryInitiator<E> extends AbstractSelectQueryFinalizer<E> implements SelectQueryInitiator<E> {

    private HavingQueryExpander<E> havingQueryExpander = new DefaultHavingQueryExpander<E>(this);
    private CriteriaQueryExpander<E> criteriaQueryExpander = new DefaultCriteriaQueryExpander<E>(this);

    public DefaultSelectQueryInitiator(DataAccessSession session, E entity) {
        super(session, entity);
    }

    @Override
    public <H, G> CrossReferenceQueryAddenda<E> union(SelectQueryExecution<H, G> query) {
        addCrossReference(query, CrossReferenceType.UNION);
        return this;
    }

    @Override
    public <H, G> CrossReferenceQueryAddenda<E> unionAll(SelectQueryExecution<H, G> query) {
        addCrossReference(query, CrossReferenceType.UNION_ALL);
        return this;
    }

    @Override
    public <H, G> CrossReferenceQueryAddenda<E> intersect(SelectQueryExecution<H, G> query) {
        addCrossReference(query, CrossReferenceType.INTERSECT);
        return this;
    }

    @Override
    public <H, G> CrossReferenceQueryAddenda<E> except(SelectQueryExecution<H, G> query) {
        addCrossReference(query, CrossReferenceType.EXCEPT);
        return this;
    }

    @Override
    public <F> OrderQueryAddenda<E> orderBy(F property, Ordering ordering) {
        if (getColumn(property) == null) {
            throw new ReferenceExpectedException("Expected to be given a reference, but got a value");
        }
        addOrdering(new ImmutableOrder(property, ordering));
        return this;
    }

    @Override
    public <F> OrderQueryAddenda<E> orderBy(F property) {
        return orderBy(property, Ordering.ASCENDING);
    }

    @Override
    public <F> HavingQueryOperation<E> openHaving(F property) {
        introduceHavingToken(Token.PARENTHESIS_OPEN);
        return having(property);
    }

    @Override
    public <F> HavingQueryOperation<E> having(F property) {
        return new DefaultHavingQueryOperation<E, F>(this, property);
    }

    @Override
    public <F> CriteriaQueryOperation<E, F> openWhere(F property) {
        introduceCriteriaToken(Token.PARENTHESIS_OPEN);
        return where(property);
    }

    @Override
    public <F> CriteriaQueryOperation<E, F> where(F property) {
        return new DefaultCriteriaQueryOperation<E, F>(this, property);
    }

    @Override
    public <F> GroupByQueryAddenda<E> groupBy(F property) {
        if (getColumn(property) == null) {
            throw new ReferenceExpectedException("Expected to be given a reference, but got a value");
        }
        addGroupByColumn(property);
        return this;
    }

    @Override
    public <G> JoinQueryAddenda<E> crossJoin(G target) {
        introduceJoin(JoinType.CROSS_JOIN, new DefaultBookKeeper<G>(target, getTableMetadata(target)), ComparisonType.IS_EQUAL_TO, null, null);
        return this;
    }

    @Override
    public <G> JoinQueryCondition<E> innerJoin(G target) {
        return new DefaultJoinQueryCondition<E, G>(this, JoinType.INNER_JOIN, target, getTableMetadata(target));
    }

    @Override
    public <G> JoinQueryCondition<E> leftOuterJoin(G target) {
        return new DefaultJoinQueryCondition<E, G>(this, JoinType.LEFT_OUTER_JOIN, target, getTableMetadata(target));
    }

    @Override
    public <G> JoinQueryCondition<E> rightOuterJoin(G target) {
        return new DefaultJoinQueryCondition<E, G>(this, JoinType.RIGHT_OUTER_JOIN, target, getTableMetadata(target));
    }

    @Override
    public <G> JoinQueryCondition<E> fullOuterJoin(G target) {
        return new DefaultJoinQueryCondition<E, G>(this, JoinType.FULL_OUTER_JOIN, target, getTableMetadata(target));
    }

    <G, F> void introduceJoin(JoinType joinType, BookKeeper<G> bookKeeper, ComparisonType comparisonType, F source, F target) {
        addJoinedSource(new ImmutableJoinedSelectionSource<G, F>(bookKeeper, joinType, source, target, comparisonType));
    }

    void introduceHavingToken(Token token) {
        addHavingToken(token);
    }

    void introduceCriteriaToken(Token token) {
        addCriteriaToken(token);
    }

    <F, G> HavingQueryExpander<E> introduceHavingComparison(ComparisonType comparisonType, F source, G target) {
        boolean hasFunction = false;
        if (source instanceof FunctionInvocation) {
            hasFunction = true;
            introduceHavingToken(new ImmutableToken(TokenType.FUNCTION, source));
        } else if (getColumn(source) != null) {
            introduceHavingToken(new ImmutableToken(TokenType.REFERENCE, source));
        } else {
            if (source == null) {
                throw new ComparisonWithoutReferenceException("NULL values comparison cannot be referenced in a HAVING clause");
            }
            introduceHavingToken(new ImmutableToken(TokenType.PARAMETER, source));
            addParameter(source, null);
        }
        introduceHavingToken(new ImmutableToken(TokenType.COMPARISON, comparisonType));
        if (target instanceof FunctionInvocation) {
            hasFunction = true;
            introduceHavingToken(new ImmutableToken(TokenType.FUNCTION, target));
        } else if (getColumn(target) != null) {
            introduceHavingToken(new ImmutableToken(TokenType.REFERENCE, target));
        } else {
            if (target == null) {
                throw new ComparisonWithoutReferenceException("NULL values comparison cannot be referenced in a HAVING clause");
            }
            introduceHavingToken(new ImmutableToken(TokenType.PARAMETER, target));
            addParameter(target, null);
        }
        if (!hasFunction) {
            throw new HavingWithoutFunctionException("At least one side of the HAVING clause of the SELECT query must be a function");
        }
        return havingQueryExpander;
    }

    <F, G> CriteriaQueryExpander<E> introduceCriteriaComparison(ComparisonType comparisonType, F source, G target) {
        if (source == null) {
            if (getColumn(target) == null) {
                throw new ComparisonWithoutReferenceException("Comparison to a NULL value must occur with a reference");
            }
        }
        if (target == null) {
            if (getColumn(source) == null) {
                throw new ComparisonWithoutReferenceException("Comparison to a NULL value must occur with a reference");
            }
        }
        if (source instanceof FunctionInvocation) {
            throw new CriteriaWithFunctionException("Functions cannot participate in the WHERE clause");
        } else if (getColumn(source) != null) {
            introduceCriteriaToken(new ImmutableToken(TokenType.REFERENCE, source));
        } else {
            introduceCriteriaToken(new ImmutableToken(TokenType.PARAMETER, source));
            addParameter(source, target);
        }
        introduceCriteriaToken(new ImmutableToken(TokenType.COMPARISON, comparisonType));
        if (target != null) {
            if (target instanceof FunctionInvocation) {
                throw new CriteriaWithFunctionException("Functions cannot participate in the WHERE clause");
            } else if (target instanceof SelectQueryExecution<?, ?>) {
                introduceCriteriaToken(new ImmutableToken(TokenType.SELECTION, target));
                addParameter(target, source);
            } else if (getColumn(target) != null) {
                introduceCriteriaToken(new ImmutableToken(TokenType.REFERENCE, target));
            } else {
                introduceCriteriaToken(new ImmutableToken(TokenType.PARAMETER, target));
                addParameter(target, source);
            }
        }
        return criteriaQueryExpander;
    }


    @Override
    public SelectQueryFinalizer<E> limit(int pageSize, int pageNumber) {
        addLimitation(pageSize, pageNumber);
        return this;
    }

}
