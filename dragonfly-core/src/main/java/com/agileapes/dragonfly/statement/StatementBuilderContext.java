/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.statement;

/**
 * This is a context for handling complex statement builder registration and dispensing.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 0:57)
 */
public interface StatementBuilderContext {

    /**
     * Returns a statement builder for the specific definition statement type (DDL)
     * @param type    the (DDL) statement type
     * @return the statement builder
     */
    StatementBuilder getDefinitionStatementBuilder(Statements.Definition type);

    /**
     * Returns a statement builder for the specific manipulation statement type (DML)
     * @param type    the (DML) statement type
     * @return the statement builder
     */
    StatementBuilder getManipulationStatementBuilder(Statements.Manipulation type);

}
