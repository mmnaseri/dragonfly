package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.basics.api.Cache;
import com.agileapes.couteau.basics.api.impl.CachingDataDispenser;
import com.agileapes.couteau.basics.api.impl.ConcurrentCache;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.entity.EntityInitializationContext;
import com.agileapes.dragonfly.error.ContextLockFailureError;

import java.io.Serializable;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/20, 16:39)
 */
public class DefaultEntityInitializationContext extends AbstractLockingEntityInitializationContext {

    private final Cache<EntityInstanceDescriptor, Object> cache = new ConcurrentCache<EntityInstanceDescriptor, Object>();

    public DefaultEntityInitializationContext(DataAccess dataAccess, EntityInitializationContext parent) {
        super(dataAccess, parent);
    }

    @Override
    protected Cache<EntityInstanceDescriptor, Object> getCache() {
        return cache;
    }
}
