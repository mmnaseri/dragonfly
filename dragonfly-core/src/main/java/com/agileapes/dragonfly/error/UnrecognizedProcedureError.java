package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 1:52)
 */
public class UnrecognizedProcedureError extends DatabaseError {

    public UnrecognizedProcedureError(Class<?> entityType, String procedureName) {
        super("No such procedure <" + procedureName + "> registered for entity <" + entityType.getCanonicalName() + ">");
    }

}
