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

package com.agileapes.dragonfly.error;

import com.agileapes.dragonfly.statement.Statements;

/**
 * This error indicates that no statement builder for the given type is available through the dialect
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 1:20)
 */
public class UnsupportedStatementTypeError extends DataAccessError {

    private static final String ERROR_MESSAGE = "Statements of type %s are not available in this context";

    public UnsupportedStatementTypeError(Statements.Definition type) {
        super(String.format(ERROR_MESSAGE, type));
    }

    public UnsupportedStatementTypeError(Statements.Manipulation type) {
        super(String.format(ERROR_MESSAGE, type));
    }

}
