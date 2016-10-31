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

package com.mmnaseri.dragonfly.data;

import com.mmnaseri.dragonfly.metadata.TableMetadata;

import java.util.Collection;

/**
 * This interface exposes methods that will handle the definition of various aspects
 * of entities
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/8, 13:59)
 */
public interface DataStructureHandler {

    /**
     * Defines the table for the entity. After calling this method, it is expected that no statements
     * would fail due to the non-existence or inconsistency of a persistent definition of the entity.
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void defineTable(Class<E> entityType);

    /**
     * Defines -- if necessary -- the primary key to the entity
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void definePrimaryKey(Class<E> entityType);

    /**
     * Defines the sequences on the entity. Note that defining sequences is not the
     * same as binding them to columns. For that, you need to call to {@link #bindSequences(Class)}
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void defineSequences(Class<E> entityType);

    /**
     * Defines the foreign keys of the entity
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void defineForeignKeys(Class<E> entityType);

    /**
     * Defines all unique constraints on the entity
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void defineUniqueConstraints(Class<E> entityType);

    /**
     * Removes the table definition for the given entity
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void removeTable(Class<E> entityType);

    /**
     * Removes primary key definitions
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void removePrimaryKeys(Class<E> entityType);

    /**
     * Removes all sequence definitions from the entity
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void removeSequences(Class<E> entityType);

    /**
     * Removes foreign key constraints
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void removeForeignKeys(Class<E> entityType);

    /**
     * Removes unique constraints
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void removeUniqueConstraints(Class<E> entityType);

    /**
     * Binds columns to their respective csequences
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void bindSequences(Class<E> entityType);

    /**
     * Unbinds sequences from the columns they were bound to
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void unbindSequences(Class<E> entityType);

    /**
     * Determines whether or not a table definition exists for the given entity
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     * @return {@code true} in case the entity has been defined
     */
    <E> boolean isDefined(Class<E> entityType);

    /**
     * Defines the table for the entity. After calling this method, it is expected that no statements
     * would fail due to the non-existence or inconsistency of a persistent definition of the entity.
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void defineTable(TableMetadata<E> entityType);

    /**
     * Defines -- if necessary -- the primary key to the entity
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void definePrimaryKey(TableMetadata<E> entityType);

    /**
     * Defines the sequences on the entity. Note that defining sequences is not the
     * same as binding them to columns. For that, you need to call to {@link #bindSequences(Class)}
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void defineSequences(TableMetadata<E> entityType);

    /**
     * Defines the foreign keys of the entity
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void defineForeignKeys(TableMetadata<E> entityType);

    /**
     * Defines all unique constraints on the entity
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void defineUniqueConstraints(TableMetadata<E> entityType);

    /**
     * Removes the table definition for the given entity
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void removeTable(TableMetadata<E> entityType);

    /**
     * Removes primary key definitions
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void removePrimaryKeys(TableMetadata<E> entityType);

    /**
     * Removes all sequence definitions from the entity
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void removeSequences(TableMetadata<E> entityType);

    /**
     * Removes foreign key constraints
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void removeForeignKeys(TableMetadata<E> entityType);

    /**
     * Removes unique constraints
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void removeUniqueConstraints(TableMetadata<E> entityType);

    /**
     * Binds columns to their respective csequences
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void bindSequences(TableMetadata<E> entityType);

    /**
     * Unbinds sequences from the columns they were bound to
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     */
    <E> void unbindSequences(TableMetadata<E> entityType);

    /**
     * Determines whether or not a table definition exists for the given entity
     * @param entityType    the type of the entity
     * @param <E>           the type of the entity
     * @return {@code true} in case the entity has been defined
     */
    <E> boolean isDefined(TableMetadata<E> entityType);

    /**
     * Initializes the data structures, ensuring that all definitions are put in place
     */
    void initialize();

    /**
     * Initializes the given set of tables
     * @param tables    the set of tables to be initialized
     */
    void initialize(Collection<TableMetadata<?>> tables);

}
