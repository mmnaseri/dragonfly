package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/28, 14:30)
 */
public class OptimisticLockingFailureError extends DatabaseError {

    public OptimisticLockingFailureError(Class<?> entityType) {
        super("Optimistic locking failure on update of entity: " + entityType.getCanonicalName());
    }

}
