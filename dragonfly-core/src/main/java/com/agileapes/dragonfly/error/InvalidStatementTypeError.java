package com.agileapes.dragonfly.error;

import com.agileapes.dragonfly.statement.StatementType;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 12:27)
 */
public class InvalidStatementTypeError extends DatabaseError {

    public InvalidStatementTypeError(StatementType statementType) {
        super("Specified statement type (" + statementType + ") is not supported in this context");
    }

}
