/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
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

package com.agileapes.dragonfly.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation specifies a default ordering for the retrieval of entities. The annotation should
 * be applied to columns, telling the data access interface that when retrieving entities without
 * specifying a key (and thus telling the framework that only one of the given entities is possible
 * to retrieve) entities should be represented in the given order.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/30, 11:07)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Order {

    /**
     * <p>Specifies the ordering applied to the given column.</p>
     *
     * <p>The following example declares a descending ordering for the {@code name} column, on the
     * retrieval of all entities of type {@code Customer}</p>
     *
     * <pre>
     *     &#064;Entity
     *     public class Customer {
     *
     *         &#064;Column
     *         &#064;Order(Ordering.DESCENDING)
     *         public String getName() {
     *             return this.name;
     *         }
     *
     *     }
     * </pre>
     */
    Ordering value() default Ordering.ASCENDING;

    /**
     * This property specifies the priority of the given ordering in regards to the ordering
     * of the fetched entities in respect with other columns.
     */
    int priority() default 0;

}
