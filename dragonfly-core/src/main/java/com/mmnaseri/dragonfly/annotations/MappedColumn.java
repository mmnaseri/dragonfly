/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mmnaseri.dragonfly.annotations;

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
 * @author Milad Naseri (mmnaseri@programmer.net)
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
