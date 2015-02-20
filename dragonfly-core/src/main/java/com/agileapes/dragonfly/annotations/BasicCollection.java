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
 * <p>This annotation indicates that the annotated getter method requires a collection of elements from a basic
 * type to be stored.</p>
 *
 * <p>These basic types include:</p>
 *
 * <ul>
 *     <li><code>java.lang.Class</code></li>
 *     <li><code>java.util.Date</code></li>
 *     <li><code>java.sql.Date</code></li>
 *     <li><code>java.sql.Time</code></li>
 *     <li><code>java.sql.Timestamp</code></li>
 *     <li><code>java.io.File</code></li>
 *     <li><code>java.net.URL</code></li>
 *     <li><code>java.net.URI</code></li>
 *     <li><code>java.lang.Integer</code></li>
 *     <li><code>java.lang.Short</code></li>
 *     <li><code>java.lang.Long</code></li>
 *     <li><code>java.lang.Double</code></li>
 *     <li><code>java.lang.Float</code></li>
 *     <li><code>java.lang.Boolean</code></li>
 *     <li><code>java.lang.Character</code></li>
 *     <li><code>java.lang.String</code></li>
 *     <li>All <code>Enum</code> values</li>
 *     <li>All primitive types (i.e. <code>char</code>, <code>boolean</code>, etc.)</li>
 * </ul>
 *
 * <p>This annotation can be used to enable reading and writing simple collections stored as properties:</p>
 *
 * <pre><code>
 *
 *     {@literal @BasicCollection}
 *     public List&lt;Date&gt; getDates() {
 *         return this.dates;
 *     }
 *
 * </code></pre>
 *
 * <p>The above example will tell the persistence API that values of the given type should be handled automatically
 * and that the given collection should be stored in a single column in the database.</p>
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/7/5 AD, 12:51)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BasicCollection {

    /**
     * @return the type of the items in the collection. Leaving it as <code>void.class</code> means that
     * the generic definition must be clear enough to allow for resolving the type argument. Setting this
     * to anything other than <code>void.class</code> will <em>override</em> the resolved type argument.
     */
    Class<?> type() default void.class;

}
