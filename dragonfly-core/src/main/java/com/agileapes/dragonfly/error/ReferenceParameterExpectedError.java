package com.agileapes.dragonfly.error;

import com.agileapes.dragonfly.data.impl.Reference;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 2:14)
 */
public class ReferenceParameterExpectedError extends DatabaseError {

    public ReferenceParameterExpectedError(Class<?> entityType, String procedureName, int parameterIndex) {
        super("Parameter " + parameterIndex + " of " + entityType.getCanonicalName() + "." + procedureName + " must be of type " + Reference.class.getCanonicalName());
    }

}
