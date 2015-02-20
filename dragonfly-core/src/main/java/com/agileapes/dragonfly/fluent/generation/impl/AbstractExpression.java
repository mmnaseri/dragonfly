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

import com.agileapes.dragonfly.fluent.error.InvalidParenthesizedGroupException;
import com.agileapes.dragonfly.fluent.generation.Expression;
import com.agileapes.dragonfly.fluent.generation.Token;
import com.agileapes.dragonfly.fluent.generation.ValueResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/10 AD, 15:27)
 */
public abstract class AbstractExpression implements Expression {

    private final List<Token> tokens = new ArrayList<Token>();
    private int open = 0;

    @Override
    public void addToken(Token token) {
        tokens.add(token);
    }

    @Override
    public void openParenthesis() {
        open ++;
        addToken(Token.PARENTHESIS_OPEN);
    }

    @Override
    public void closeParenthesis() {
        open --;
        if (open < 0) {
            throw new InvalidParenthesizedGroupException("Parenthesis closed without being opened first");
        }
        addToken(Token.PARENTHESIS_CLOSE);
    }

    @Override
    public String evaluate(ValueResolver valueResolver) {
        if (open > 0) {
            throw new InvalidParenthesizedGroupException("There are parentheses left unclosed");
        }
        return doEvaluate(new ArrayList<Token>(tokens), valueResolver);
    }

    protected abstract String doEvaluate(List<Token> tokens, ValueResolver valueResolver);
}
