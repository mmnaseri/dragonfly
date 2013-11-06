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

package com.agileapes.dragonfly.annotations;

/**
 * This enum holds different parameter modes.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 0:33)
 */
public enum ParameterMode {

    /**
     * The parameter's value will be read by the procedure
     */
    IN,
    /**
     * The procedure will write, as part of its output, into the specified
     * variable. The argument passed for this parameter must be of type
     * {@link com.agileapes.dragonfly.data.impl.Reference}
     */
    OUT,
    /**
     * The procedure will read the argument value and pass output data into
     * the parameter. The argument passed for this parameter must be of type
     * {@link com.agileapes.dragonfly.data.impl.Reference}
     */
    IN_OUT

}
