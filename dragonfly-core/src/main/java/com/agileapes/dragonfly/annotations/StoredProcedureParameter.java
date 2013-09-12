package com.agileapes.dragonfly.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 0:33)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface StoredProcedureParameter {

    ParameterMode mode() default ParameterMode.IN;

    Class<?> type();

}
