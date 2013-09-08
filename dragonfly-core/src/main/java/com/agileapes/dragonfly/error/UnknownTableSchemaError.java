package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 18:35)
 */
public class UnknownTableSchemaError extends DatabaseError {

    public UnknownTableSchemaError(Class<?> entityType) {
        super("No schema was defined for entity: " + entityType);
    }
}
