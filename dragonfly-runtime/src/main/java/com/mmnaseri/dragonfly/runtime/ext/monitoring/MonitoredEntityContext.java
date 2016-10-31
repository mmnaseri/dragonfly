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
import com.mmnaseri.dragonfly.runtime.ext.monitoring.impl.History;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/28 AD, 15:20)
 */
public interface MonitoredEntityContext {

    List<Map<String,Object>> executeQuery(Class<?> entityType, String queryName, Map<String, Object> values);

    void executeUpdate(Class<?> entityType, String queryName, Map<String, Object> values);

    List<Map<String,Object>> executeQuery(Class<?> entityType, String queryName, Object value);

    void executeUpdate(Class<?> entityType, String queryName, Object value);

    <E, K extends Serializable> List<E> executeQuery(Class<E> entityType, String queryName, History<E, K> history);

    <E, K extends Serializable> void executeUpdate(Class<E> entityType, String queryName, History<E, K> history);

    <E, K extends Serializable> List<E> findAll(Class<E> entityType, K key);

    <E, K extends Serializable> List<E> findBefore(Class<E> entityType, K key, Date date);

    <E, K extends Serializable> List<E> findAfter(Class<E> entityType, K key, Date date);

    <E, K extends Serializable> List<E> findBefore(Class<E> entityType, K key, Serializable version);

    <E, K extends Serializable> List<E> findAfter(Class<E> entityType, K key, Serializable version);

    <E, K extends Serializable> List<E> findBetween(Class<E> entityType, K key, Serializable from, Serializable to);

    <E, K extends Serializable> List<E> findBetween(Class<E> entityType, K key, Date from, Date to);

    <E, K extends Serializable> List<E> findByOperation(Class<E> entityType, K key, OperationType operationType);

    <E, K extends Serializable> E find(Class<E> entityType, K key, Serializable version);

    <E, K extends Serializable> E revert(Class<E> entityType, K key, Serializable version);

    <E> List<E> findAll(E sample);

    <E> List<E> findBefore(E sample, Date date);

    <E> List<E> findAfter(E sample, Date date);

    <E> List<E> findBefore(E sample, Serializable version);

    <E> List<E> findAfter(E sample, Serializable version);

    <E> List<E> findBetween(E sample, Serializable from, Serializable to);

    <E> List<E> findBetween(E sample, Date from, Date to);

    <E> List<E> findByOperation(E sample, OperationType operationType);

    <E> E find(E sample, Serializable version);

    <E> E revert(E sample, Serializable version);

    boolean hasHistory(Object entity);

    <E> void note(OperationType operationType, E entity);
}
