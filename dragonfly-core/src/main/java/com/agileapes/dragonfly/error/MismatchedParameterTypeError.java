package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 2:11)
 */
public class MismatchedParameterTypeError extends DatabaseError {

    public MismatchedParameterTypeError(Class<?> entityType, String procedureName, int parameterIndex, Class<?> expectedType, Class<?> actualType) {
        super("Expected parameter " + parameterIndex + " of " + entityType.getCanonicalName() + "." + procedureName + " to be of type " + entityType.getClass() + " while it was of type " + actualType.getClass());
    }

}
