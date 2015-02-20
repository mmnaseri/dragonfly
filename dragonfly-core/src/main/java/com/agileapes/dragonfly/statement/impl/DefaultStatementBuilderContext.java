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

package com.agileapes.dragonfly.statement.impl;

import com.agileapes.dragonfly.error.UnsupportedStatementTypeError;
import com.agileapes.dragonfly.statement.StatementBuilder;
import com.agileapes.dragonfly.statement.StatementBuilderContext;
import com.agileapes.dragonfly.statement.Statements;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a builder context that performs storage and access for different statement builders.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 1:16)
 */
public class DefaultStatementBuilderContext implements StatementBuilderContext {

    private final Map<Statements.Definition, StatementBuilder> definitionStatements = new ConcurrentHashMap<Statements.Definition, StatementBuilder>();
    private final Map<Statements.Manipulation, StatementBuilder> manipulationStatements = new ConcurrentHashMap<Statements.Manipulation, StatementBuilder>();

    @Override
    public StatementBuilder getDefinitionStatementBuilder(Statements.Definition type) {
        if (definitionStatements.containsKey(type)) {
            return definitionStatements.get(type);
        }
        throw new UnsupportedStatementTypeError(type);
    }

    @Override
    public StatementBuilder getManipulationStatementBuilder(Statements.Manipulation type) {
        if (manipulationStatements.containsKey(type)) {
            return manipulationStatements.get(type);
        }
        throw new UnsupportedStatementTypeError(type);
    }

    public void register(Statements.Definition type, StatementBuilder statementBuilder) {
        definitionStatements.put(type, statementBuilder);
    }

    public void register(Statements.Manipulation type, StatementBuilder statementBuilder) {
        manipulationStatements.put(type, statementBuilder);
    }

}
