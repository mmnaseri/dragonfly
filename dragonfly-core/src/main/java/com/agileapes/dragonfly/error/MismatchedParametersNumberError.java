/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.error;

/**
 * Raised when the number of parameters passed for a procedure call does not match the number
 * of arguments defined for it.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 1:56)
 */
public class MismatchedParametersNumberError extends DataAccessError {

    public MismatchedParametersNumberError(Class<?> entityType, String procedureName, int expectedParameters, int actualParameters) {
        super("Expected " + expectedParameters + " parameters for procedure " + entityType.getCanonicalName() + "." + procedureName + " but was given " + actualParameters);
    }

}
