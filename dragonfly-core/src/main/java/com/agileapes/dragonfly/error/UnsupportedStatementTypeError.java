package com.agileapes.dragonfly.error;

import com.agileapes.dragonfly.statement.StatementType;
import com.agileapes.dragonfly.statement.Statements;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 1:20)
 */
public class UnsupportedStatementTypeError extends DatabaseError {

    private static final String ERROR_MESSAGE = "Statements of type %s are not available in this context";

    public UnsupportedStatementTypeError(Statements.Definition type) {
        super(String.format(ERROR_MESSAGE, type));
    }

    public UnsupportedStatementTypeError(Statements.Manipulation type) {
        super(String.format(ERROR_MESSAGE, type));
    }

    public UnsupportedStatementTypeError(StatementType statementType) {
        super(String.format(ERROR_MESSAGE, statementType));
    }
}
