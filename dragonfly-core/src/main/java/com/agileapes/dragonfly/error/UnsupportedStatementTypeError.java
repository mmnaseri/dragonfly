package com.agileapes.dragonfly.error;

import com.agileapes.dragonfly.statement.Statements;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 1:20)
 */
public class UnsupportedStatementTypeError extends DatabaseError {

    public UnsupportedStatementTypeError(Statements.Definition type) {
        super("Statements of type " + type + " are not available in this context");
    }

    public UnsupportedStatementTypeError(Statements.Manipulation type) {
        super("Statements of type " + type + " are not available in this context");
    }

}
