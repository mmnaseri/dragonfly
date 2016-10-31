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

import com.mmnaseri.couteau.basics.api.Processor;
import com.agileapes.dragonfly.sample.test.Expectation;
import com.agileapes.dragonfly.sample.test.ExpectationContext;
import com.agileapes.dragonfly.sample.test.ExpectationFinalizer;
import com.agileapes.dragonfly.sample.test.error.ExpectationFailureException;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/30 AD, 15:03)
 */
public class DefaultExpectationContext implements ExpectationContext {

    private final Set<Expectation<?>> expectations = new CopyOnWriteArraySet<Expectation<?>>();

    @Override
    public <E> ExpectationFinalizer<E> setupExpectation(final Expectation<E> expectation) {
        expectations.add(expectation);
        return new DefaultExpectationController<E>(new Processor<Expectation<E>>() {
            @Override
            public void process(Expectation<E> input) {
                expectations.remove(expectation);
            }
        });
    }

    @Override
    public void run() {
        if (!expectations.isEmpty()) {
            throw new ExpectationFailureException("There are " + expectations.size() + " unterminated expectations");
        }
    }

}
