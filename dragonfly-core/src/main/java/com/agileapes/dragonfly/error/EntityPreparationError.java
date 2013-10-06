package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/22, 12:36)
 */
public class EntityPreparationError extends DatabaseError {

    public EntityPreparationError(String msg) {
        super(msg);
    }

    public EntityPreparationError(String msg, Throwable cause) {
        super(msg, cause);
    }

}
