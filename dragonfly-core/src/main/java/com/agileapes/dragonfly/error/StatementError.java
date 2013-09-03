package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/1, 1:27)
 */
public class StatementError extends DatabaseError {

    public StatementError(String message) {
        super(message);
    }

    public StatementError(String message, Throwable cause) {
        super(message, cause);
    }

}