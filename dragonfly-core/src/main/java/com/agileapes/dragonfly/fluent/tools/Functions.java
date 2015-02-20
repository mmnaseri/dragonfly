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

package com.agileapes.dragonfly.fluent.tools;

import com.agileapes.dragonfly.fluent.generation.Function;
import com.agileapes.dragonfly.fluent.generation.FunctionInvocation;
import com.agileapes.dragonfly.fluent.generation.impl.ImmutableFunction;
import com.agileapes.dragonfly.fluent.generation.impl.ImmutableFunctionInvocation;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/10 AD, 15:43)
 */
public abstract class Functions {

    private static Function<Long> count = new ImmutableFunction<Long>(Long.class, "COUNT", 1);
    private static Function<Number> sum = new ImmutableFunction<Number>(Number.class, "SUM", 1);
    private static Function<Comparable> min = new ImmutableFunction<Comparable>(Comparable.class, "MIN", 1);
    private static Function<Comparable> max = new ImmutableFunction<Comparable>(Comparable.class, "MAX", 1);
    private static Function<Long> length = new ImmutableFunction<Long>(Long.class, "LENGTH", 1);
    private static Function<Number> round = new ImmutableFunction<Number>(Number.class, "ROUND", 1);
    private static Function<Number> average = new ImmutableFunction<Number>(Number.class, "AVG", 1);

    public static <F> FunctionInvocation<Long> count(F value) {
        return new ImmutableFunctionInvocation<Long>(count, value);
    }

    public static <F> FunctionInvocation<Number> sum(F value) {
        return new ImmutableFunctionInvocation<Number>(sum, value);
    }

    public static <F> FunctionInvocation<Comparable> min(F value) {
        return new ImmutableFunctionInvocation<Comparable>(min, value);
    }

    public static <F> FunctionInvocation<Comparable> max(F value) {
        return new ImmutableFunctionInvocation<Comparable>(max, value);
    }

    public static <F> FunctionInvocation<Long> length(F value) {
        return new ImmutableFunctionInvocation<Long>(length, value);
    }

    public static <F> FunctionInvocation<Number> round(F value) {
        return new ImmutableFunctionInvocation<Number>(round, value);
    }

    public static <F> FunctionInvocation<Number> average(F value) {
        return new ImmutableFunctionInvocation<Number>(average, value);
    }

}
