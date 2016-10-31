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

import com.mmnaseri.couteau.context.impl.OrderedBeanComparator;
import com.mmnaseri.dragonfly.events.DataAccessEventHandler;
import com.mmnaseri.dragonfly.events.EventHandlerContext;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * This event handler is capable of taking different handler instances, and calling to them
 * one by one, in a chained fashion. The handlers are sorted, if the implement the
 * {@link com.mmnaseri.couteau.context.contract.OrderedBean} interface
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/9, 1:32)
 */
public class CompositeDataAccessEventHandler implements DataAccessEventHandler, EventHandlerContext {

    private List<DataAccessEventHandler> handlers = new CopyOnWriteArrayList<DataAccessEventHandler>();

    @Override
    public void addHandler(DataAccessEventHandler eventHandler) {
        handlers.add(eventHandler);
        synchronized (this) {
            handlers = with(handlers).sort(new OrderedBeanComparator()).concurrentList();
        }
    }

    @Override
    public <E> void beforeSave(E entity) {
        for (DataAccessEventHandler handler : handlers) {
            handler.beforeSave(entity);
        }
    }

    @Override
    public <E> void afterSave(E entity) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterSave(entity);
        }
    }

    @Override
    public <E> void beforeInsert(E entity) {
        for (DataAccessEventHandler handler : handlers) {
            handler.beforeInsert(entity);
        }
    }

    @Override
    public <E> void afterInsert(E entity) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterInsert(entity);
        }
    }

    @Override
    public <E> void beforeUpdate(E entity) {
        for (DataAccessEventHandler handler : handlers) {
            handler.beforeUpdate(entity);
        }
    }

    @Override
    public <E> void afterUpdate(E entity, boolean updated) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterUpdate(entity, updated);
        }
    }

    @Override
    public <E> void beforeDelete(E entity) {
        for (DataAccessEventHandler handler : handlers) {
            handler.beforeDelete(entity);
        }
    }

    @Override
    public <E> void afterDelete(E entity) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterDelete(entity);
        }
    }

    @Override
    public <E, K extends Serializable> void beforeDelete(Class<E> entityType, K key) {
        for (DataAccessEventHandler handler : handlers) {
            handler.beforeDelete(entityType, key);
        }
    }

    @Override
    public <E, K extends Serializable> void afterDelete(Class<E> entityType, K key) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterDelete(entityType, key);
        }
    }

    @Override
    public <E> void beforeDeleteAll(Class<E> entityType) {
        for (DataAccessEventHandler handler : handlers) {
            handler.beforeDeleteAll(entityType);
        }
    }

    @Override
    public <E> void afterDeleteAll(Class<E> entityType) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterDeleteAll(entityType);
        }
    }

    @Override
    public <E> void beforeTruncate(Class<E> entityType) {
        for (DataAccessEventHandler handler : handlers) {
            handler.beforeTruncate(entityType);
        }
    }

    @Override
    public <E> void afterTruncate(Class<E> entityType) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterTruncate(entityType);
        }
    }

    @Override
    public <E> void beforeFind(E sample) {
        for (DataAccessEventHandler handler : handlers) {
            handler.beforeFind(sample);
        }
    }

    @Override
    public <E> void afterFind(E sample, List<E> entities) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterFind(sample, entities);
        }
    }

    @Override
    public <E, K extends Serializable> void beforeFind(Class<E> entityType, K key) {
        for (DataAccessEventHandler handler : handlers) {
            handler.beforeFind(entityType, key);
        }
    }

    @Override
    public <E, K extends Serializable> E afterFind(Class<E> entityType, K key, E entity) {
        E found = entity;
        for (DataAccessEventHandler handler : handlers) {
            found = handler.afterFind(entityType, key, found);
        }
        return found;
    }

    @Override
    public <E> void beforeFindAll(Class<E> entityType) {
        for (DataAccessEventHandler handler : handlers) {
            handler.beforeFindAll(entityType);
        }
    }

    @Override
    public <E> void afterFindAll(Class<E> entityType, List<E> entities) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterFindAll(entityType, entities);
        }
    }

    @Override
    public <E> void beforeExecuteUpdate(Class<E> entityType, String queryName, Map<String, Object> values) {
        for (DataAccessEventHandler handler : handlers) {
            handler.beforeExecuteUpdate(entityType, queryName, values);
        }
    }

    @Override
    public <E> void afterExecuteUpdate(Class<E> entityType, String queryName, Map<String, Object> values, int affectedRows) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterExecuteUpdate(entityType, queryName, values, affectedRows);
        }
    }

    @Override
    public <E> void beforeExecuteUpdate(E sample, String queryName) {
        for (DataAccessEventHandler handler : handlers) {
            handler.beforeExecuteUpdate(sample, queryName);
        }
    }

    @Override
    public <E> void afterExecuteUpdate(E sample, String queryName, int affectedRows) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterExecuteUpdate(sample, queryName, affectedRows);
        }
    }

    @Override
    public <E> void beforeExecuteQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        for (DataAccessEventHandler handler : handlers) {
            handler.beforeExecuteQuery(entityType, queryName, values);
        }
    }

    @Override
    public <E> void afterExecuteQuery(Class<E> entityType, String queryName, Map<String, Object> values, List<E> entities) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterExecuteQuery(entityType, queryName, values, entities);
        }
    }

    @Override
    public <E> void beforeExecuteQuery(E sample, String queryName) {
        for (DataAccessEventHandler handler : handlers) {
            handler.beforeExecuteQuery(sample, queryName);
        }
    }

    @Override
    public <E> void afterExecuteQuery(E sample, String queryName, List<E> entities) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterExecuteQuery(sample, queryName, entities);
        }
    }

}
