package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:32)
 */
public class EntityInitializationError extends DatabaseError {

    public EntityInitializationError(Class<?> entityType, Throwable cause) {
        super("There was an error initializing bean of type: " + entityType + ". It is " +
                "possible that your entity is not a POJO.", cause);
    }

}
