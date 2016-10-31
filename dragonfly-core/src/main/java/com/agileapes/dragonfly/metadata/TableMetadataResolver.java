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

package com.agileapes.dragonfly.metadata;

import com.mmnaseri.couteau.basics.api.Filter;

/**
 * This interface defines a way through which metadata can be resolved for a given entity.
 * It also can deny resolving metadata for a given entity, as it can easily return {@code false}
 * for the {@link Filter#accepts(Object)} method inherited from {@link Filter}
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 12:55)
 */
public interface TableMetadataResolver extends Filter<Class<?>> {

    /**
     * Resolves metadata for the given entity type. Note that calling this method on a given
     * entity must be done after calling to {@link #accepts(Object)} to check whether or not
     * the method will be actually determining proper table metadata.
     * @param entityType    the type of the entity
     * @param <E>           the type parameter for the entity
     * @return table metadata for the entity, as resolved through the method
     */
    <E> TableMetadata<E> resolve(Class<E> entityType);

}
