package com.agileapes.dragonfly.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>This annotation specifies a mapping between a partial entity, and a column from
 * a result set.</p>
 *
 * <p>Example: Mapping column <em>cnt</em> to property <em>count</em>:</p>
 *
 * <pre>
 *
 *    {@literal @Partial}
 *     public class CustomerMetadata {
 *
 *         private long count;
 *
 *        {@literal @MappedColumn}(column = "cnt")
 *         public long getCount() {
 *             return this.count;
 *         }
 *
 *         public void setCount(long count) {
 *             this.count = count;
 *         }
 *
 *     }
 *
 * </pre>
 *
 * <p>This way, by executing a partial query for this entity, and querying for a column
 * named <em>cnt</em>, you can get its value in the <em>count</em> property.</p>
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/7, 13:07)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MappedColumn {

    /**
     * The column name to which this property is bound.
     */
    String column() default "";

    /**
     * Whether the existence of this property is optional in the result set.
     * By setting it to <code>false</code>, you are announcing that you expect
     * to have a value for this property, or the execution is going to fail.
     */
    boolean optional() default true;

}
