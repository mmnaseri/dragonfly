/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.data;

import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>This interface exposes the main functionalities expected from a data access layer
 * interface.</p>
 *
 * <p>This is essentially the core API at the heart of this framework, which enables
 * users to access the database seamlessly and without direct contact.</p>
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 19:06)
 */
public interface DataAccess {

    /**
     * <p>Attempts to persist the given entity. This is done either by inserting the new
     * entity into the database, or by updating an existing entity.</p>
     *
     * <p>An update is triggered if and only if exactly one entity sharing the state of
     * the given entity is found. For entities with a key, this can be simplified to sharing
     * the key value.</p>
     * @param entity    the entity to be persisted. This entity must belong to the data access
     *                  interface's defining context, i.e., it must be initialized through
     *                  {@link EntityContext#getInstance(Class)} or {@link EntityContext#getInstance(TableMetadata)}
     * @param <E>       the type of the entity
     * @return the saved entity, which might be identical to the passed entity
     */
    <E> E save(E entity);

    /**
     * This will save the entity to the database as a new entity. No heuristics need apply, since
     * we are explicitly asking for a new entry to be added to the database.
     * @param entity    the entity to be persisted. This entity must belong to the data access
     *                  interface's defining context, i.e., it must be initialized through
     *                  {@link EntityContext#getInstance(Class)} or {@link EntityContext#getInstance(TableMetadata)}
     * @param <E>       the type of the entity
     * @return the saved entity, which might be identical to the passed entity
     */
    <E> E insert(E entity);

    /**
     * Updates the given entity in the database. Might result in more than one row being updated.
     * @param entity    the entity to be persisted. This entity must belong to the data access
     *                  interface's defining context, i.e., it must be initialized through
     *                  {@link EntityContext#getInstance(Class)} or {@link EntityContext#getInstance(TableMetadata)}
     * @param <E>       the type of the entity
     * @return the saved entity, which might be identical to the passed entity
     */
    <E> E update(E entity);

    /**
     * <p>Attempts to delete the given entity. This is done by taking the given
     * entity as a sample. If it can be properly identified with a primary key,
     * then only one item will be deleted. If not, however, <em>any</em> item matching the
     * item at hand will be deleted.</p>
     * @param entity    the entity to be deleted. This entity must belong to the data access
     *                  interface's defining context, i.e., it must be initialized through
     *                  {@link EntityContext#getInstance(Class)} or {@link EntityContext#getInstance(TableMetadata)}
     * @param <E>       the type of the entity
     */
    <E> void delete(E entity);

    /**
     * Attempts to delete an item of the given type, with the given key.
     * @param entityType    the type of the entity. This must be an entity for which
     *                      table metadata has been previously decided, or can be
     *                      readily determined.
     * @param key           the key to the item.
     * @param <E>           the type of the entity.
     * @param <K>           the type of the key.
     */
    <E, K extends Serializable> void delete(Class<E> entityType, K key);

    /**
     * Deletes all items in the table associated with the entity type.
     * @param entityType    the type of the entity. This must be an entity for which
     *                      table metadata has been previously decided, or can be
     *                      readily determined.
     * @param <E>           the type of the entity.
     */
    <E> void deleteAll(Class<E> entityType);

    /**
     * Empties the items in the table from the given type. <strong>Be warned</strong> that in most implementations
     * of the different database engines available, while this is (probably much) faster than calling to {@link #deleteAll(Class)},
     * it is also not undoable and in the context of a transaction cannot be rolled back.
     * @param entityType    the type of the entity. This must be an entity for which
     *                      table metadata has been previously decided, or can be
     *                      readily determined.
     * @param <E>           the type of the entity.
     */
    <E> void truncate(Class<E> entityType);

    /**
     * Finds all matching items in the database.
     * @param sample    the entity to be used as a sample. This entity must belong to the data access
     *                  interface's defining context, i.e., it must be initialized through
     *                  {@link EntityContext#getInstance(Class)} or {@link EntityContext#getInstance(TableMetadata)}
     * @param <E>       the type of the entity.
     * @return a list of all items matching the description as provided by the sample.
     */
    <E> List<E> find(E sample);

    /**
     * Finds all matching items in the database.
     * @param sample    the entity to be used as a sample. This entity must belong to the data access
     *                  interface's defining context, i.e., it must be initialized through
     *                  {@link EntityContext#getInstance(Class)} or {@link EntityContext#getInstance(TableMetadata)}
     * @param order     the order expression for the results
     * @param <E>       the type of the entity.
     * @return a list of all items matching the description as provided by the sample.
     * @see com.agileapes.dragonfly.data.impl.OrderExpressionParser
     */
    <E> List<E> find(E sample, String order);

    /**
     * Attempts to find a single entry in the database matching the given type and having
     * the designated key.
     * @param entityType    the type of the entity. This must be an entity for which
     *                      table metadata has been previously decided, or can be
     *                      readily determined.
     * @param key           the key to the entity
     * @param <E>           the type of the entity
     * @param <K>           the type of the key
     * @return the item with the given key
     */
    <E, K extends Serializable> E find(Class<E> entityType, K key);

    /**
     * Attempts to list all items with the given type registered with the database accessible
     * to the current session
     * @param entityType    the type of the entity to be enlisted
     * @param <E>           the type of the entity
     * @return a list of all available items
     */
    <E> List<E> findAll(Class<E> entityType);

    /**
     * Attempts to list all items with the given type registered with the database accessible
     * to the current session
     * @param entityType    the type of the entity to be enlisted
     * @param order     the order expression for the results
     * @param <E>           the type of the entity
     * @return a list of all available items
     * @see com.agileapes.dragonfly.data.impl.OrderExpressionParser
     */
    <E> List<E> findAll(Class<E> entityType, String order);

    /**
     * Executes the named query for the item, injecting values as necessary from the
     * provided map
     * @param entityType    the type of the entity. This must be an entity for which
     *                      table metadata has been previously decided, or can be
     *                      readily determined.
     * @param queryName     the name of the query to be executed. This query has to be
     *                      of a non-returning (update) type.
     * @param values        the values to be used while processing the query.
     * @param <E>           the type of the entity.
     * @return the number of items affected by this query.
     */
    <E> int executeUpdate(Class<E> entityType, String queryName, Map<String, Object> values);

    /**
     * Executes the named query for the item, injecting values as necessary from the
     * provided sample
     * @param sample        the entity to be used as a sample. This entity must belong to the data access
     *                      interface's defining context, i.e., it must be initialized through
     *                      {@link EntityContext#getInstance(Class)} or {@link EntityContext#getInstance(TableMetadata)}
     * @param queryName     the name of the query to be executed. This query has to be
     *                      of a non-returning (update) type.
     * @param <E>           the type of the entity.
     * @return the number of items affected by this query.
     */
    <E> int executeUpdate(E sample, String queryName);

    /**
     * Executes the query for the given type, using the query named and injecting values
     * into the query with help from the specified map of values as necessary.
     * @param entityType    the type of the entity. This must be an entity for which
     *                      table metadata has been previously decided, or can be
     *                      readily determined.
     * @param queryName     the name of the query to be executed. This query has to be
     *                      of a value-returning (query) type.
     * @param values        the map of values to be used for value injection
     * @param <E>           the type of the items
     * @return the list of items matching the criteria of the query
     */
    <E> List<E> executeQuery(Class<E> entityType, String queryName, Map<String, Object> values);

    /**
     * Executes the query for the given type, using the query named and injecting values
     * into the query with help from the specified sample as necessary.
     * @param sample        the entity to be used as a sample. This entity must belong to the data access
     *                      interface's defining context, i.e., it must be initialized through
     *                      {@link EntityContext#getInstance(Class)} or {@link EntityContext#getInstance(TableMetadata)}
     * @param queryName     the name of the query to be executed. This query has to be
     *                      of a value-returning (query) type.
     * @param <E>           the type of the items
     * @return the list of items matching the criteria of the query
     */
    <E> List<E> executeQuery(E sample, String queryName);

    /**
     * Executes the given procedure on the server side, returning any generated results
     * @param entityType       the type of the entity for which the procedure is defined
     * @param procedureName    the name of the procedure
     * @param parameters       the parameters to the procedure. Note that parameters corresponding
     *                         to {@link com.agileapes.dragonfly.annotations.ParameterMode#OUT} and
     *                         {@link com.agileapes.dragonfly.annotations.ParameterMode#IN_OUT} must
     *                         be of type {@link com.agileapes.dragonfly.data.impl.Reference} with the generics type specified to be
     *                         matching that of the defined parameter
     * @param <E>              the type of the entity over which the call is taking place
     * @return the result of the call of an empty list if no result is returned
     */
    <E> List<?> call(Class<E> entityType, String procedureName, Object... parameters);

    /**
     * Counts all items of the given type
     * @param entityType       the type of the entity for which the procedure is defined
     * @param <E>              the type of the entity over which the call is taking place
     * @return the number of items from the given type in the database
     */
    <E> long countAll(Class<E> entityType);

    /**
     * Counts items matching the given sample
     * @param sample    the sample to be counted
     * @param <E>       the type of the item
     * @return number of matching items in the persistent storage
     */
    <E> long count(E sample);

    /**
     * Determines if an item matching the given sample exists in the database
     * @param sample    the sample to be checked
     * @param <E>       the type of the item
     * @return {@code true} in case the item exists in the database
     */
    <E> boolean exists(E sample);

    /**
     * Checks whether an item of the given type with the given key exists in the database
     * @param entityType    the type of the entity. This must be an entity for which
     *                      table metadata has been previously decided, or can be
     *                      readily determined.
     * @param key           the key to the item
     * @param <E>           the type of the items to be controlled
     * @param <K>           the type of the key
     * @return boolean value specifying whether or not the entity specified exists
     */
    <E, K extends Serializable> boolean exists(Class<E> entityType, K key);

    /**
     * Runs the operations specified through the callback in batches so that
     * all similar operations are executed sequentially and through the same
     * batch operation
     * @param batchOperation    the stack of operations to be executed
     * @return the result of the update operations
     */
    List<Integer> run(BatchOperation batchOperation);

}
