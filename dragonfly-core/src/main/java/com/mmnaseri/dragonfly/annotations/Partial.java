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
 * @author Milad Naseri (mmnaseri@programmer.net)
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
