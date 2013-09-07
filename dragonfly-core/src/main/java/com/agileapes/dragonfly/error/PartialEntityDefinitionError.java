package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/7, 13:11)
 */
public class PartialEntityDefinitionError extends DatabaseError {
    public PartialEntityDefinitionError(String message) {
        super(message);
    }

    public PartialEntityDefinitionError(String message, Throwable cause) {
        super(message, cause);
    }
}
