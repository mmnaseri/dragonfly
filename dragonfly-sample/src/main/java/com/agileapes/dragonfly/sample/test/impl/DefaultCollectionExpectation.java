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

package com.agileapes.dragonfly.sample.test.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.sample.test.CollectionExpectation;
import com.agileapes.dragonfly.sample.test.ExpectationContext;

import java.util.Collection;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/30 AD, 15:47)
 */
public class DefaultCollectionExpectation<I, E extends Collection<I>> extends DefaultExpectation<E> implements CollectionExpectation<I, E> {

    private final ExpectationContext context;
    private final boolean negate;
    private final E value;

    public DefaultCollectionExpectation(ExpectationContext context, boolean negate, E value) {
        super(context, negate, value);
        this.context = context;
        this.negate = negate;
        this.value = value;
    }

    @Override
    public CollectionExpectation<I, E> not() {
        close();
        return new DefaultCollectionExpectation<I, E>(context, !negate, value);
    }

    @Override
    public void toBeEmpty() {
        check(value.isEmpty(), "%s == 0", value.size());
    }

    @Override
    public void toContain(I item) {
        check(value.contains(item), "%s in collection", item);
    }

    @Override
    public void toContain(Filter<I> matcher) {
        check(with(value).exists(matcher), "collection to have item matching the given description");
    }

    @Override
    public void toHaveSize(int size) {
        check(value.size() == size, "%s == %s", value.size(), size);
    }

}
