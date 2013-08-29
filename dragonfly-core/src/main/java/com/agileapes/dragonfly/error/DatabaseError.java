package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:38)
 */
public class DatabaseError extends Error {

    public DatabaseError(String message) {
        super(message);
    }

    public DatabaseError(String message, Throwable cause) {
        super(message, cause);
    }
}
