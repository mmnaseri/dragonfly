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
