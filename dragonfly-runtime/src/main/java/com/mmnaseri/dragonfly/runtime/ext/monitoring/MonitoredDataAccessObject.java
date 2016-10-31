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

package com.mmnaseri.dragonfly.runtime.ext.monitoring;

import com.mmnaseri.dragonfly.data.OperationType;
import com.mmnaseri.dragonfly.entity.EntityAware;
import com.mmnaseri.dragonfly.entity.EntityContextAware;
import com.mmnaseri.dragonfly.entity.EntityHandlerAware;
import com.mmnaseri.dragonfly.runtime.ext.monitoring.impl.History;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Turns a row into its monitored interface, where from a user can interact with and query
 * on the history of the row
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/27 AD, 12:02)
 */
public interface MonitoredDataAccessObject<E, K extends Serializable> extends EntityAware, EntityHandlerAware<E>, MonitoredEntityContextAware, EntityContextAware {

    /**
     * @return all the history associated with the entity so far
     */
    List<E> findAll();

    /**
     * Finds all the history of the entity prior to a certain point in time
     * @param date    the date of the enquiry
     * @return the relevant history
     */
    List<E> findBefore(Date date);

    /**
     * Finds all the history of the entity after a certain point in time
     * @param date    the date of the enquiry
     * @return the relevant history
     */
    List<E> findAfter(Date date);

    /**
     * Finds all the history of the entity in a given range
     * @param from      the beginning of the range
     * @param to        the end of the range
     * @return the relevant history
     */
    List<E> findBetween(Date from, Date to);

    /**
     * Finds all history of the entity based on a certain operation
     * @param operationType    the type of the operation
     * @return the relevant history
     */
    List<E> findByOperation(OperationType operationType);

    /**
     * Finds the history of the entity prior to it getting the given version
     * @param version    the subject of the enquiry
     * @return the relevant history
     */
    List<E> findBefore(Serializable version);

    /**
     * Finds the history of the entity after it getting the given version
     * @param version    the subject of the enquiry
     * @return the relevant history
     */
    List<E> findAfter(Serializable version);

    /**
     * Finds all the history of the entity in a given range
     * @param from      the beginning of the range
     * @param to        the end of the range
     * @return the relevant history
     */
    List<E> findBetween(Serializable from, Serializable to);

    /**
     * Finds an entity as it appeared in a certain version
     * @param version    the subject of the enquiry
     * @return the relevant history or {@code null} if no such history exists
     */
    E find(Serializable version);

    /**
     * Reverts the entity (along-side the relations in which it is the owner) to the
     * given version
     * @param version    the subject of the enquiry
     */
    void revert(Serializable version);

    /**
     * Runs the specified query on the entity
     * @param queryName    the name of the query
     * @param values       the map of values for the entity
     * @return the relevant history
     */
    List<E> query(String queryName, Map<String, Object> values);

    /**
     * Runs the specified query on the entity
     * @param queryName    the name of the query
     * @param history      the history descriptor for the object
     * @return the relevant history
     */
    List<Object> query(String queryName, History<E, K> history);

}
