package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/7, 1:31)
 */
public class ObjectNotFoundError extends DatabaseError {

    public ObjectNotFoundError(Class<?> entityType, Object key) {
        super("No object of type " + entityType.getCanonicalName() + " with key <" + key + "> was found");
    }

}
