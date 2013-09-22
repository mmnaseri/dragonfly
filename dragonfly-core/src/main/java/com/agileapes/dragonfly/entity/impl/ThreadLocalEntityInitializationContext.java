package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.basics.api.Cache;
import com.agileapes.couteau.basics.api.impl.SimpleCache;
import com.agileapes.dragonfly.data.DataAccess;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/21, 13:45)
 */
public class ThreadLocalEntityInitializationContext extends AbstractLockingEntityInitializationContext {

    private final ThreadLocal<Cache<EntityInstanceDescriptor, Object>> threadLocalCache = new ThreadLocal<Cache<EntityInstanceDescriptor, Object>>() {
        @Override
        protected Cache<EntityInstanceDescriptor, Object> initialValue() {
            return new SimpleCache<EntityInstanceDescriptor, Object>();
        }
    };

    public ThreadLocalEntityInitializationContext(DataAccess dataAccess) {
        super(dataAccess, null);
    }

    @Override
    protected synchronized Cache<EntityInstanceDescriptor, Object> getCache() {
        return threadLocalCache.get();
    }
}
