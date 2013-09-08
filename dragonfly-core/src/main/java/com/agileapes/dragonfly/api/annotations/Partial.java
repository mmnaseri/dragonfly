package com.agileapes.dragonfly.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>This annotation lets you bind properties of an arbitrary, non-persistent entity to
 * values as specified in a database.</p>
 *
 * <p>This annotation is used in conjunction with {@link MappedColumn} to specify and
 * designate said mappings.</p>
 *
 * <p>You must bind a partial entity to a certain named query by specifying the entity
 * for which the named query is defined and the name of that query.</p>
 *
 * @see MappedColumn
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/7, 13:09)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Partial {

    /**
     * The entity whose named query will be used to retrieve the values of this object.
     * Both this and {@link #query()} must be set for the feature to work.
     */
    Class<?> targetEntity();

    /**
     * The name of the query to be used. Both this and the {@link #targetEntity()} must be
     * set for the feature to work.
     */
    String query();

}
