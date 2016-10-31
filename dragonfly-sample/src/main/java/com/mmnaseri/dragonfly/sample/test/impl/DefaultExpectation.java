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

package com.mmnaseri.dragonfly.sample.test.impl;

import com.mmnaseri.dragonfly.sample.test.Expectation;
import com.mmnaseri.dragonfly.sample.test.ExpectationContext;
import com.mmnaseri.dragonfly.sample.test.ExpectationFinalizer;
import com.mmnaseri.dragonfly.sample.test.error.ExpectationFailureException;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/30 AD, 15:12)
 */
public class DefaultExpectation<E> implements Expectation<E> {

    private final ExpectationContext context;
    private final boolean negate;
    private final E value;
    private final ExpectationFinalizer<E> finalizer;

    public DefaultExpectation(ExpectationContext context, boolean negate, E value) {
        finalizer = context.setupExpectation(this);
        this.context = context;
        this.negate = negate;
        this.value = value;
    }

    @Override
    public Expectation<E> not() {
        close();
        return new DefaultExpectation<E>(context, !negate, value);
    }

    protected void close() {
        finalizer.close(this);
    }

    @Override
    public void toBe(E value) {
        check(this.value == value, "%s == %s", value, this.value);
    }

    @Override
    public void toEqual(E value) {
        check(this.value.equals(value), "%s.equals(%s)", value, this.value);
    }

    @Override
    public void toBeNull() {
        check(this.value == null, "%s == null", this.value);
    }

    @Override
    public void toBeTrue() {
        check(value instanceof Boolean && (Boolean) value, "%s == true", value);
    }

    @Override
    public void toBeFalse() {
        check(value instanceof Boolean && !(Boolean) value, "%s == false", value);
    }

    protected void check(boolean condition, String message, Object... values) {
        close();
        if (negate) {
            message = "!(" + message + ")";
        }
        if (!condition && !negate || condition && negate) {
            throw new ExpectationFailureException("Expectation failed: " + String.format(message, values));
        }
    }

}
