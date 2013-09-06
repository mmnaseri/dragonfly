package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/7, 1:32)
 */
public class AmbiguousObjectKeyError extends DatabaseError {

    public AmbiguousObjectKeyError(Class<?> entityType, Object key) {
        super("Key <" + key + "> is ambiguous for describing objects of type " + entityType);
    }

}
