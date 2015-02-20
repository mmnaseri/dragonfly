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

package com.agileapes.dragonfly.data.impl;

import com.agileapes.dragonfly.data.BatchOperation;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.fluent.SelectQueryInitiator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/21 AD, 16:22)
 */
public class MemoryDataAccess implements DataAccess {

    private final ThreadLocal<Collection<Object>> storage = new ThreadLocal<Collection<Object>>(){
        @Override
        protected Collection<Object> initialValue() {
            return new ArrayList<Object>();
        }
    };

    @Override
    public <E> E save(E entity) {
        return null;
    }

    @Override
    public <E> E insert(E entity) {
        return null;
    }

    @Override
    public <E> E update(E entity) {
        return null;
    }

    @Override
    public <E> void delete(E entity) {

    }

    @Override
    public <E, K extends Serializable> void delete(Class<E> entityType, K key) {

    }

    @Override
    public <E> void deleteAll(Class<E> entityType) {

    }

    @Override
    public <E> void truncate(Class<E> entityType) {

    }

    @Override
    public <E> List<E> find(E sample) {
        return null;
    }

    @Override
    public <E> List<E> find(E sample, String order) {
        return null;
    }

    @Override
    public <E> List<E> find(E sample, int pageSize, int pageNumber) {
        return null;
    }

    @Override
    public <E> List<E> find(E sample, String order, int pageSize, int pageNumber) {
        return null;
    }

    @Override
    public <E, K extends Serializable> E find(Class<E> entityType, K key) {
        return null;
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType) {
        return null;
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType, String order) {
        return null;
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType, String order, int pageSize, int pageNumber) {
        return null;
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType, int pageSize, int pageNumber) {
        return null;
    }

    @Override
    public <E> int executeUpdate(Class<E> entityType, String queryName, Map<String, Object> values) {
        return 0;
    }

    @Override
    public <E> int executeUpdate(E sample, String queryName) {
        return 0;
    }

    @Override
    public <E> List<E> executeQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        return null;
    }

    @Override
    public <E> List<E> executeQuery(E sample, String queryName) {
        return null;
    }

    @Override
    public <E> List<?> call(Class<E> entityType, String procedureName, Object... parameters) {
        return null;
    }

    @Override
    public <E> long countAll(Class<E> entityType) {
        return 0;
    }

    @Override
    public <E> long count(E sample) {
        return 0;
    }

    @Override
    public <E> boolean exists(E sample) {
        return false;
    }

    @Override
    public <E, K extends Serializable> boolean exists(Class<E> entityType, K key) {
        return false;
    }

    @Override
    public List<Integer> run(BatchOperation batchOperation) {
        return null;
    }

    @Override
    public <E> SelectQueryInitiator<E> from(E alias) {
        throw new UnsupportedOperationException("Fluent API is not accessible for in-memory usage");
    }

}
