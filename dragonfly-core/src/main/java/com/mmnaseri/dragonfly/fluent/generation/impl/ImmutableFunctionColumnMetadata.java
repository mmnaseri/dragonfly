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

package com.mmnaseri.dragonfly.fluent.generation.impl;

import com.mmnaseri.dragonfly.fluent.error.UnexpectedNumberOfArgumentsException;
import com.mmnaseri.dragonfly.fluent.generation.FunctionColumnMetadata;
import com.mmnaseri.dragonfly.fluent.generation.FunctionInvocation;
import com.mmnaseri.dragonfly.metadata.impl.ResolvedColumnMetadata;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/10 AD, 17:59)
 */
public class ImmutableFunctionColumnMetadata extends ResolvedColumnMetadata implements FunctionColumnMetadata {

    private final FunctionInvocation invocation;

    public ImmutableFunctionColumnMetadata(FunctionInvocation invocation) {
        super(null, null, null, 0, null, null, false, 0, 0, 0, null, null, false, false);
        this.invocation = invocation;
    }

    @Override
    public FunctionInvocation getInvocation() {
        return invocation;
    }

    @Override
    public String apply(String... arguments) {
        if (arguments.length != invocation.getFunction().getArgumentsCount()) {
            throw new UnexpectedNumberOfArgumentsException(invocation.getFunction().getFunctionName(), invocation.getFunction().getArgumentsCount(), arguments.length);
        }
        final StringBuilder builder = new StringBuilder();
        builder.append(invocation.getFunction().getFunctionName());
        builder.append("(");
        for (String argument : arguments) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(argument);
        }
        builder.append(")");
        return builder.toString();
    }

}
