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
import com.agileapes.dragonfly.fluent.*;
import com.agileapes.dragonfly.fluent.generation.Token;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/10 AD, 16:21)
 */
public class DefaultCriteriaQueryExpander<E> implements CriteriaQueryExpander<E> {

    private final DefaultSelectQueryInitiator<E> initiator;

    public DefaultCriteriaQueryExpander(DefaultSelectQueryInitiator<E> initiator) {
        this.initiator = initiator;
    }

    @Override
    public <F> CriteriaQueryOperation<E, F> and(F property) {
        initiator.introduceCriteriaToken(Token.AND);
        return initiator.where(property);
    }

    @Override
    public <F> CriteriaQueryOperation<E, F> or(F property) {
        initiator.introduceCriteriaToken(Token.OR);
        return initiator.where(property);
    }

    @Override
    public <F> CriteriaQueryOperation<E, F> andOpen(F property) {
        initiator.introduceCriteriaToken(Token.AND);
        return initiator.openWhere(property);
    }

    @Override
    public <F> CriteriaQueryOperation<E, F> orOpen(F property) {
        initiator.introduceCriteriaToken(Token.OR);
        return initiator.openWhere(property);
    }

    @Override
    public CriteriaQueryExpander<E> close() {
        initiator.introduceCriteriaToken(Token.PARENTHESIS_CLOSE);
        return this;
    }

    @Override
    public <F> OrderQueryAddenda<E> orderBy(F property, Ordering ordering) {
        return initiator.orderBy(property, ordering);
    }

    @Override
    public <F> OrderQueryAddenda<E> orderBy(F property) {
        return initiator.orderBy(property);
    }

    @Override
    public <F> GroupByQueryAddenda<E> groupBy(F property) {
        return initiator.groupBy(property);
    }

    @Override
    public <F> HavingQueryOperation<E> openHaving(F property) {
        return initiator.openHaving(property);
    }

    @Override
    public <F> HavingQueryOperation<E> having(F property) {
        return initiator.having(property);
    }

    @Override
    public List<? extends E> select() {
        return initiator.select();
    }

    @Override
    public <H> List<? extends H> select(H binding) {
        return initiator.select(binding);
    }

    @Override
    public <H> List<? extends H> selectDistinct(H binding) {
        return initiator.selectDistinct(binding);
    }

    @Override
    public List<? extends E> selectDistinct() {
        return initiator.selectDistinct();
    }

    @Override
    public <H> SelectQueryExecution<E, H> selection(H binding) {
        return initiator.selection(binding);
    }

    @Override
    public SelectQueryExecution<E, E> selection() {
        return initiator.selection();
    }

    @Override
    public <H> SelectQueryExecution<E, H> distinctSelection(H binding) {
        return initiator.distinctSelection(binding);
    }

    @Override
    public SelectQueryExecution<E, E> distinctSelection() {
        return initiator.distinctSelection();
    }

    @Override
    public <H, G> CrossReferenceQueryAddenda<E> union(SelectQueryExecution<H, G> query) {
        return initiator.union(query);
    }

    @Override
    public <H, G> CrossReferenceQueryAddenda<E> unionAll(SelectQueryExecution<H, G> query) {
        return initiator.unionAll(query);
    }

    @Override
    public <H, G> CrossReferenceQueryAddenda<E> intersect(SelectQueryExecution<H, G> query) {
        return initiator.intersect(query);
    }

    @Override
    public <H, G> CrossReferenceQueryAddenda<E> except(SelectQueryExecution<H, G> query) {
        return initiator.except(query);
    }

    @Override
    public SelectQueryFinalizer<E> limit(int pageSize, int pageNumber) {
        return initiator.limit(pageSize, pageNumber);
    }
}
