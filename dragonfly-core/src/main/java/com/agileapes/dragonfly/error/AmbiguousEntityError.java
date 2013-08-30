package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/30, 15:51)
 */
public class AmbiguousEntityError extends DatabaseError {

    private final Class<?> entityType;

    public AmbiguousEntityError(Class<?> entityType) {
        super("Ambiguous entity definition for entity " + entityType.getCanonicalName());
        this.entityType = entityType;
    }

    public Class<?> getEntityType() {
        return entityType;
    }
}
