package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 20:10)
 */
public class EntityDeletedError extends DatabaseError {

    public EntityDeletedError() {
        super("The entity you are working with has already been deleted.");
    }

}
