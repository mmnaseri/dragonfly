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

package com.agileapes.dragonfly.events;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This interface allows for handling different event pointcuts for all data access events. Events
 * elicited by any extending interfaces for the {@link com.agileapes.dragonfly.data.DataAccess}
 * interface are not covered in this event handler.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 1:15)
 */
public interface DataAccessEventHandler {

    /**
     * This method is called right before a save action is triggered. This is triggered by a call
     * to {@link com.agileapes.dragonfly.data.DataAccess#save(Object)}, which will in turn result
     * in an insert or update operation. So, if no errors occur, a <code>beforeSave</code> event
     * should immediately result in either a <code>beforeInsert</code> or <code>beforeUpdate</code>
     * event.
     * @param entity    the entity being saved
     * @param <E>       the type parameter for the entity
     */
    <E> void beforeSave(E entity);

    /**
     * Occurs after the entity has been saved, and has been duly populated with any generated
     * keys
     * @param entity    the entity
     * @param <E>       the type parameter for the entity
     */
    <E> void afterSave(E entity);

    /**
     * Event signifies the start of an insert operation
     * @param entity    the entity
     * @param <E>       the type parameter for the entity
     */
    <E> void beforeInsert(E entity);

    /**
     * Event is raised after an insert operation has completed successfully
     * @param entity    the entity
     * @param <E>       the type parameter for the entity
     */
    <E> void afterInsert(E entity);

    /**
     * Signifies the start of an update operation
     * @param entity    the entity
     * @param <E>       the type parameter for the entity
     */
    <E> void beforeUpdate(E entity);

    /**
     * Determines that an update operation has been carried out without any problems
     * @param entity    the entity
     * @param updated   value determining whether or not the update affected the persistent
     *                  version of the entity
     * @param <E>       the type parameter for the entity
     */
    <E> void afterUpdate(E entity, boolean updated);

    /**
     * Occurs at the start of a delete operation
     * @param entity    the entity
     * @param <E>       the type parameter for the entity
     */
    <E> void beforeDelete(E entity);

    /**
     * Determines that a delete operation has finished successfully
     * @param entity    the entity
     * @param <E>       the type parameter for the entity
     */
    <E> void afterDelete(E entity);

    /**
     * Determines that a specific entity in the database is being removed physically
     * @param entityType    the type of the entity being operated on
     * @param key           the key specifying the exact identity of the entity in question
     * @param <E>           the type parameter for the entity
     * @param <K>           the type parameter for the key
     */
    <E, K extends Serializable> void beforeDelete(Class<E> entityType, K key);

    /**
     * Signifies that an entity has been just removed from the database
     * @param entityType    the type of the entity being operated on
     * @param key           the key specifying the exact identity of the entity in question
     * @param <E>           the type parameter for the entity
     * @param <K>           the type parameter for the key
     */
    <E, K extends Serializable> void afterDelete(Class<E> entityType, K key);

    /**
     * Signifies that all entities of a given type are being removed
     * @param entityType    the type of the entity being operated on
     * @param <E>           the type parameter for the entity
     */
    <E> void beforeDeleteAll(Class<E> entityType);

    /**
     * This event tells the listeners that all entities of a given type have been just removed
     * @param entityType    the type of the entity being operated on
     * @param <E>           the type parameter for the entity
     */
    <E> void afterDeleteAll(Class<E> entityType);

    /**
     * This event is raised when a table for the given type is being truncated
     * @param entityType    the type of the entity being operated on
     * @param <E>           the type parameter for the entity
     */
    <E> void beforeTruncate(Class<E> entityType);

    /**
     * This event is raised whenever a truncation is finished
     * @param entityType    the type of the entity being operated on
     * @param <E>           the type parameter for the entity
     */
    <E> void afterTruncate(Class<E> entityType);

    /**
     * This event is to signify that all entities matching a given sample are required to be fetched
     * @param sample    the sample for which the operation was conducted
     * @param <E>       the type parameter for the entity
     */
    <E> void beforeFind(E sample);

    /**
     * This event is raised whenever the find operation returns from the database
     * @param sample    the sample for which the operation was conducted
     * @param entities  the entities loaded in response to the find operation
     * @param <E>       the type parameter for the entity
     */
    <E> void afterFind(E sample, List<E> entities);

    /**
     * This event signifies looking for a specific row in the database
     * @param entityType    the type of the entity being operated on
     * @param key           the key specifying the exact identity of the entity in question
     * @param <E>           the type parameter for the entity
     * @param <K>           the type parameter for the key
     */
    <E, K extends Serializable> void beforeFind(Class<E> entityType, K key);

    /**
     * This event is triggered whenever a find operation finishes
     * @param entityType    the type of the entity being operated on
     * @param key           the key specifying the exact identity of the entity in question
     * @param entity        the entity found through the search, or {@code null} if nothing
     *                      is found
     * @param <E>           the type parameter for the entity
     * @param <K>           the type parameter for the key
     * @return the (modified) entity found. This will be returned as the found entity to the
     * calling user
     */
    <E, K extends Serializable> E afterFind(Class<E> entityType, K key, E entity);

    /**
     * This event is run whenever all entities of a given type are sought after
     * @param entityType    the type of the entity being operated on
     * @param <E>           the type parameter for the entity
     */
    <E> void beforeFindAll(Class<E> entityType);

    /**
     * This event occurs when entities of a given type have been listed
     * @param entityType    the type of the entity being operated on
     * @param entities      the entities loaded in response to the find operation
     * @param <E>           the type parameter for the entity
     */
    <E> void afterFindAll(Class<E> entityType, List<E> entities);

    /**
     * This event is raised when a named update statement is being executed
     * @param entityType    the type of the entity being operated on
     * @param queryName     the name of the query being executed
     * @param values        values passed to interpolate the query
     * @param <E>           the type parameter for the entity
     */
    <E> void beforeExecuteUpdate(Class<E> entityType, String queryName, Map<String, Object> values);

    /**
     * This event is raised whenever a named update statement has finished its execution
     * @param entityType    the type of the entity being operated on
     * @param queryName     the name of the query being executed
     * @param values        values passed to interpolate the query
     * @param affectedRows  the number of rows  affected through the query
     * @param <E>           the type parameter for the entity
     */
    <E> void afterExecuteUpdate(Class<E> entityType, String queryName, Map<String, Object> values, int affectedRows);

    /**
     * The event signifies an update statement for a sample
     * @param sample        the sample for which the operation was conducted
     * @param queryName     the name of the query being executed
     * @param <E>           the type parameter for the entity
     */
    <E> void beforeExecuteUpdate(E sample, String queryName);

    /**
     * The event signifies the end of an update operation for a sample
     * @param sample        the sample for which the operation was conducted
     * @param queryName     the name of the query being executed
     * @param affectedRows  the number of rows affected through the query
     * @param <E>           the type parameter for the entity
     */
    <E> void afterExecuteUpdate(E sample, String queryName, int affectedRows);

    /**
     * This event is raised when a named query is being executed
     * @param entityType    the type of the entity being operated on
     * @param queryName     the name of the query being executed
     * @param values        values passed to interpolate the query
     * @param <E>           the type parameter for the entity
     */
    <E> void beforeExecuteQuery(Class<E> entityType, String queryName, Map<String, Object> values);

    /**
     * This event is raised when a named query statement is finished
     * @param entityType    the type of the entity being operated on
     * @param queryName     the name of the query being executed
     * @param values        values passed to interpolate the query
     * @param entities      the entities loaded by the query
     * @param <E>           the type parameter for the entity
     */
    <E> void afterExecuteQuery(Class<E> entityType, String queryName, Map<String, Object> values, List<E> entities);

    /**
     * This event signifies the start of a named query statement for a given sample
     * @param sample        the sample for which the operation was conducted
     * @param queryName     the name of the query being executed
     * @param <E>           the type parameter for the entity
     */
    <E> void beforeExecuteQuery(E sample, String queryName);

    /**
     * This method is called to signify the end of the fetching of all entities matching the given
     * sample based on the named query specified through the data access method call
     * @param sample        the sample for which the operation was conducted
     * @param queryName     the name of the query being executed
     * @param entities      the entities loaded in response to the find operation
     * @param <E>           the type parameter for the entity
     */
    <E> void afterExecuteQuery(E sample, String queryName, List<E> entities);

}
