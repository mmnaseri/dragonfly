package com.agileapes.dragonfly.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>This annotation is designed as a replacement for JPA 1.0's lack of a
 * clear definition of a call to a stored procedure.</p>
 *
 * <p>The annotation defines a call to a procedure that is in the same schema
 * as the entity on which the annotation is placed.</p>
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 0:31)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StoredProcedure {

    /**
     * The name of the procedure to be called
     */
    String name();

    /**
     * The result type of the call, if need be.
     */
    Class<?> resultType() default void.class;

    /**
     * The parameters to the call
     */
    StoredProcedureParameter[] parameters() default {};

}
