package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.entity.EntityInitializationContext;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/21, 13:45)
 */
public class ThreadLocalEntityInitializationContext implements EntityInitializationContext {

    private final Map<Thread, EntityInitializationContext> initializationContexts = new ConcurrentHashMap<Thread, EntityInitializationContext>();
    private final DataAccess dataAccess;

    public ThreadLocalEntityInitializationContext(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    private synchronized EntityInitializationContext getInitializationContext() {
        final Thread thread = Thread.currentThread();
        if (!initializationContexts.containsKey(thread)) {
            initializationContexts.put(thread, new DefaultEntityInitializationContext(dataAccess));
        }
        return initializationContexts.get(thread);
    }

    @Override
    public <E> void delete(Class<E> entityType, Serializable key) {
        getInitializationContext().delete(entityType, key);
    }

    @Override
    public <E> void register(Class<E> entityType, Serializable key, E entity) {
        getInitializationContext().register(entityType, key, entity);
    }

    @Override
    public <E> E get(Class<E> entityType, Serializable key) {
        return getInitializationContext().get(entityType, key);
    }

}
