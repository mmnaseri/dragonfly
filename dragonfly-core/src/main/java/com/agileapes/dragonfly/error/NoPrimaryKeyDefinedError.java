package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:38)
 */
public class NoPrimaryKeyDefinedError extends DatabaseError {

    public NoPrimaryKeyDefinedError(Class<?> entityType) {
        super("No primary key has been defined for: " + entityType.getCanonicalName());
    }

}
