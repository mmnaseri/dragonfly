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

package com.mmnaseri.dragonfly.sample.cases;

import com.mmnaseri.dragonfly.sample.test.*;
import com.mmnaseri.dragonfly.sample.test.error.ExpectationFailureException;
import com.mmnaseri.dragonfly.sample.test.impl.DefaultCollectionExpectation;
import com.mmnaseri.dragonfly.sample.test.impl.DefaultExpectation;
import com.mmnaseri.dragonfly.sample.test.impl.DefaultExpectationContext;
import com.mmnaseri.dragonfly.sample.test.impl.DefaultInvocationExpectation;

import java.util.Collection;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/30 AD, 14:56)
 */
public abstract class BaseTestCase implements TestCase {

    private final ExpectationContext context;

    protected BaseTestCase() {
        context = new DefaultExpectationContext();
    }

    @Override
    public <E> Expectation<E> expect(E value) {
        return new DefaultExpectation<E>(context, false, value);
    }

    @Override
    public InvocationExpectation<Invocation> expect(Invocation invocation) {
        return new DefaultInvocationExpectation<Invocation>(context, false, invocation);
    }

    @Override
    public <I, E extends Collection<I>> CollectionExpectation<I, E> expect(E collection) {
        return new DefaultCollectionExpectation<I, E>(context, false, collection);
    }

    @Override
    public final void execute() throws ExpectationFailureException {
        try {
            run();
        } catch (Throwable e) {
            throw new ExpectationFailureException("Expected test case not to throw", e);
        }
        context.run();
    }

}
