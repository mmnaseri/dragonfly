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

package com.mmnaseri.dragonfly.fluent;

import com.mmnaseri.dragonfly.annotations.Ordering;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/9 AD, 16:43)
 */
public interface OrderQueryAddenda<E> extends CrossReferenceQueryAddenda<E> {

    /**
     * Adds an ORDER BY clause to the query
     * @param property    the property on which the ordering applies
     * @param ordering    the ordering
     * @param <F>         the type of the property
     * @return the addenda for expanding the query
     */
    <F> OrderQueryAddenda<E> orderBy(F property, Ordering ordering);

    /**
     * Adds an ORDER BY clause to the query with the default ordering {@link Ordering#ASCENDING ASC}
     * @param property    the property on which the ordering applies
     * @param <F>         the type of the property
     * @return the addenda for expanding the query
     */
    <F> OrderQueryAddenda<E> orderBy(F property);

}
