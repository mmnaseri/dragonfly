package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/21, 23:22)
 */
public class ContextLockFailureError extends DatabaseError {

    public ContextLockFailureError() {
        super("Database lock has been released without being captured first.");
    }

}
