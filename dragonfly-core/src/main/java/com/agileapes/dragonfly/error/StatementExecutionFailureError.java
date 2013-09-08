package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 2:02)
 */
public class StatementExecutionFailureError extends DatabaseError {

    public StatementExecutionFailureError(String message, Throwable cause) {
        super(message, cause);
    }

}
