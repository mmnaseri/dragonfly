package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 12:59)
 */
public class EntityDefinitionError extends DatabaseError {

    public EntityDefinitionError(String message) {
        super(message);
    }

    public EntityDefinitionError(String message, Throwable cause) {
        super(message, cause);
    }

}
