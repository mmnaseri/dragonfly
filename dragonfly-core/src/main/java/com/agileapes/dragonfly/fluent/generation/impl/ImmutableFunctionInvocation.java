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

package com.agileapes.dragonfly.fluent.generation.impl;

import com.agileapes.dragonfly.fluent.generation.Function;
import com.agileapes.dragonfly.fluent.generation.FunctionInvocation;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/10 AD, 15:47)
 */
public class ImmutableFunctionInvocation<R> implements FunctionInvocation<R> {

    private final static AtomicLong COUNTER = new AtomicLong(0);
    private final Function<R> function;
    private final String alias;
    private final Object[] arguments;

    public ImmutableFunctionInvocation(Function<R> function, Object... arguments) {
        this.function = function;
        this.arguments = arguments;
        this.alias = function.getFunctionName() + String.valueOf(COUNTER.getAndIncrement());
    }

    @Override
    public Function<R> getFunction() {
        return function;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public R getResult() {
        return null;
    }

    @Override
    public String getAlias() {
        return alias;
    }

}
