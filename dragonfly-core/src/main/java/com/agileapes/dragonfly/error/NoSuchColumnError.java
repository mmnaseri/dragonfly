package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 13:36)
 */
public class NoSuchColumnError extends DatabaseError {

    public NoSuchColumnError(Class<?> entityType, String columnName) {
        super("No such column was found: " + entityType.getCanonicalName() + "." + columnName);
    }

}
