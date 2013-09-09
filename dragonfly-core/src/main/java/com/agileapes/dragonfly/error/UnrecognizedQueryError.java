package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 2:10)
 */
public class UnrecognizedQueryError extends DatabaseError {

    public UnrecognizedQueryError(Class<?> entityType, String queryName) {
        super("No such query <" + queryName + "> for entity " + entityType.getCanonicalName());
    }

}
