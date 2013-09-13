package com.agileapes.dragonfly.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation defines a parameter to the procedure call
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 0:33)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface StoredProcedureParameter {

    /**
     * The mode of the parameter
     */
    ParameterMode mode() default ParameterMode.IN;

    /**
     * Java type for the given parameter. Temporal times must use the specific
     * subclass of {@link java.util.Date} under {@code java.sql.*}.
     */
    Class<?> type();

}
