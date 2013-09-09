package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 1:29)
 */
public class NoSuchProcedureError extends DatabaseError {

    public NoSuchProcedureError(Class<?> entityType, String procedureName) {
        super("No such procedure was defined: " + entityType.getCanonicalName() + "." + procedureName);
    }

}
