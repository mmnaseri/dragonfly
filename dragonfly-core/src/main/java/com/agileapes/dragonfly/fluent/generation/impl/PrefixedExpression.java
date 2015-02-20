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

import com.agileapes.dragonfly.fluent.SelectQueryExecution;
import com.agileapes.dragonfly.fluent.generation.ComparisonType;
import com.agileapes.dragonfly.fluent.generation.ConjunctionType;
import com.agileapes.dragonfly.fluent.generation.Token;
import com.agileapes.dragonfly.fluent.generation.ValueResolver;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/11 AD, 17:06)
 */
public class PrefixedExpression extends AbstractExpression {

    private final String prefix;

    public PrefixedExpression(String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected String doEvaluate(List<Token> tokens, ValueResolver valueResolver) {
        if (tokens.isEmpty()) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        builder.append(" ").append(prefix).append(" ");
        for (Token token : tokens) {
            switch (token.getType()) {
                case PARENTHESIS_OPEN:
                    builder.append("(");
                    break;
                case PARENTHESIS_CLOSE:
                    builder.append(")");
                    break;
                case PARAMETER:
                    builder.append("?");
                    break;
                case FUNCTION:
                case REFERENCE:
                    builder.append(valueResolver.resolve(token.getValue(), false, true));
                    break;
                case SELECTION:
                    builder.append("(").append(((SelectQueryExecution) token.getValue()).getSql()).append(")");
                    break;
                case COMPARISON:
                    builder.append(" ").append(((ComparisonType) token.getValue()).getOperator()).append(" ");
                    break;
                case CONJUNCTION:
                    builder.append(" ").append(((ConjunctionType) token.getValue()).name()).append(" ");
                    break;
            }
        }
        return builder.toString();
    }

}
