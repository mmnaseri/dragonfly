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

package com.mmnaseri.dragonfly.entity;

import com.mmnaseri.dragonfly.data.DataAccess;
import com.mmnaseri.dragonfly.metadata.TableMetadata;

/**
 * This interface abstracts the concept of an entity context, where entity instances come from.
 * The entity context is supposed to generate instances, manage them, and keep track of them.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/5, 15:11)
 */
public interface EntityContext {

    /**
     * Dispenses an instance of the entity
     * @param entityType    the entity type to be instantiated
     * @param <E>           the type of the entity
     * @return the generated entity instance
     */
    <E> E getInstance(Class<E> entityType);

    /**
     * Dispenses an instance of the entity associated with the given table metadata
     * @param tableMetadata    the table metadata
     * @param <E>              the type of the entity
     * @return the generated entity instance
     */
    <E> E getInstance(TableMetadata<E> tableMetadata);

    /**
     * Determines whether or not the given entity has come from this context.
     * @param entity    te entity to be checked
     * @param <E>       the type of the entity
     * @return {@code true} if {@link #getInstance(Class)} or {@link #getInstance(TableMetadata)}
     * was used to access the instance.
     */
    <E> boolean has(E entity);

    /**
     * Will initialize the entity context for the given data access instance
     * @param dataAccess    the data access instance to use for the entity context
     */
    void initialize(DataAccess dataAccess);

    /**
     * Determines whether or not the context has been initialized
     * @return {@code true} means it has already been initialized once
     */
    boolean isInitialized();

}
