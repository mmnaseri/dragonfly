package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/30, 15:49)
 */
public class NoSuchEntityError extends DatabaseError {

    private final Class<?> entityType;

    public NoSuchEntityError(Class<?> entityType) {
        super("No metadata found for entity " + entityType.getCanonicalName());
        this.entityType = entityType;
    }

    public Class<?> getEntityType() {
        return entityType;
    }
}
