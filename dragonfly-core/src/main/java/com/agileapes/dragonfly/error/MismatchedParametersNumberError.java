package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 1:56)
 */
public class MismatchedParametersNumberError extends DatabaseError {

    public MismatchedParametersNumberError(Class<?> entityType, String procedureName, int expectedParameters, int actualParameters) {
        super("Expected " + expectedParameters + " parameters for procedure " + entityType.getCanonicalName() + "." + procedureName + " but was given " + actualParameters);
    }

}
