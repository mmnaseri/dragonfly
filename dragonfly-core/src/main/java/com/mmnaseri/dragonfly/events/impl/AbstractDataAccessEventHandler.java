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

package com.mmnaseri.dragonfly.events.impl;

import com.mmnaseri.dragonfly.events.DataAccessEventHandler;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This class allows for singular implementation of only the required methods of the
 * event handler
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/9, 1:31)
 */
public abstract class AbstractDataAccessEventHandler implements DataAccessEventHandler {

    @Override
    public <E> void beforeSave(E entity) {
    }

    @Override
    public <E> void afterSave(E entity) {
    }

    @Override
    public <E> void beforeInsert(E entity) {
    }

    @Override
    public <E> void afterInsert(E entity) {
    }

    @Override
    public <E> void beforeUpdate(E entity) {
    }

    @Override
    public <E> void afterUpdate(E entity, boolean updated) {
    }

    @Override
    public <E> void beforeDelete(E entity) {
    }

    @Override
    public <E> void afterDelete(E entity) {
    }

    @Override
    public <E, K extends Serializable> void beforeDelete(Class<E> entityType, K key) {
    }

    @Override
    public <E, K extends Serializable> void afterDelete(Class<E> entityType, K key) {
    }

    @Override
    public <E> void beforeDeleteAll(Class<E> entityType) {
    }

    @Override
    public <E> void afterDeleteAll(Class<E> entityType) {
    }

    @Override
    public <E> void beforeTruncate(Class<E> entityType) {
    }

    @Override
    public <E> void afterTruncate(Class<E> entityType) {
    }

    @Override
    public <E> void beforeFind(E sample) {
    }

    @Override
    public <E> void afterFind(E sample, List<E> entities) {
    }

    @Override
    public <E, K extends Serializable> void beforeFind(Class<E> entityType, K key) {
    }

    @Override
    public <E, K extends Serializable> E afterFind(Class<E> entityType, K key, E entity) {
        return entity;
    }

    @Override
    public <E> void beforeFindAll(Class<E> entityType) {
    }

    @Override
    public <E> void afterFindAll(Class<E> entityType, List<E> entities) {
    }

    @Override
    public <E> void beforeExecuteUpdate(Class<E> entityType, String queryName, Map<String, Object> values) {
    }

    @Override
    public <E> void afterExecuteUpdate(Class<E> entityType, String queryName, Map<String, Object> values, int affectedRows) {
    }

    @Override
    public <E> void beforeExecuteUpdate(E sample, String queryName) {
    }

    @Override
    public <E> void afterExecuteUpdate(E sample, String queryName, int affectedRows) {
    }

    @Override
    public <E> void beforeExecuteQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
    }

    @Override
    public <E> void afterExecuteQuery(Class<E> entityType, String queryName, Map<String, Object> values, List<E> entities) {
    }

    @Override
    public <E> void beforeExecuteQuery(E sample, String queryName) {
    }

    @Override
    public <E> void afterExecuteQuery(E sample, String queryName, List<E> entities) {
    }

}
