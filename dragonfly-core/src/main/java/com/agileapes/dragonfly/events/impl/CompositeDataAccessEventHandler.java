package com.agileapes.dragonfly.events.impl;

import com.agileapes.couteau.context.impl.OrderedBeanComparator;
import com.agileapes.dragonfly.api.DataAccess;
import com.agileapes.dragonfly.events.DataAccessEventHandler;
import com.agileapes.dragonfly.events.EventHandlerContext;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
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
    public <E> void afterUpdate(E entity) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterUpdate(entity);
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
    public <E, K extends Serializable> void afterFind(Class<E> entityType, K key, E entity) {
        for (DataAccessEventHandler handler : handlers) {
            handler.afterFind(entityType, key, entity);
        }
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
