package com.agileapes.dragonfly.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines an extension to the normal definition of any entity
 * matching the descriptor
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 14:39)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Extension {

    String ALL = "*";

    /**
     * This specifier allows for writing elaborate filters as to which entities are
     * subject to the present extension. Simply leaving it as is (<code>*</code>) means
     * the extension will apply to all entities.
     */
    String filter() default ALL;

}
