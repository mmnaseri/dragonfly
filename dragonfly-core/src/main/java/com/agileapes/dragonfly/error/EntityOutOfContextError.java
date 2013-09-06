package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 19:18)
 */
public class EntityOutOfContextError extends DatabaseError {

    public EntityOutOfContextError(Class<?> entityType) {
        super("Given entity of type " + entityType.getCanonicalName() + " is out of context");
    }

}
