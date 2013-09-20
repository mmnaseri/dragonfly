package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.basics.api.Cache;
import com.agileapes.couteau.basics.api.impl.CachingDataDispenser;
import com.agileapes.couteau.basics.api.impl.ConcurrentCache;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.entity.EntityInitializationContext;

import java.io.Serializable;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/20, 16:39)
 */
public class DefaultEntityInitializationContext extends CachingDataDispenser<DefaultEntityInitializationContext.EntityInstanceDescriptor, Object> implements EntityInitializationContext {

    public static class EntityInstanceDescriptor {

        private final Class<?> entityType;
        private final Serializable key;

        private EntityInstanceDescriptor(Class<?> entityType, Serializable key) {
            this.entityType = entityType;
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EntityInstanceDescriptor that = (EntityInstanceDescriptor) o;
            return entityType.equals(that.entityType) && key.equals(that.key);

        }

        @Override
        public int hashCode() {
            int result = entityType.hashCode();
            result = 31 * result + key.hashCode();
            return result;
        }
    }

    private final Cache<EntityInstanceDescriptor, Object> cache = new ConcurrentCache<EntityInstanceDescriptor, Object>();
    private final DataAccess dataAccess;

    public DefaultEntityInitializationContext(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    protected Cache<EntityInstanceDescriptor, Object> getCache() {
        return cache;
    }

    @Override
    protected Object produce(EntityInstanceDescriptor key) {
        return dataAccess.find(key.entityType, key.key);
    }

    @Override
    public <E> void register(Class<E> entityType, Serializable key, E entity) {
        write(getDescriptor(entityType, key), entity);
    }

    private <E> EntityInstanceDescriptor getDescriptor(Class<?> entityType, Serializable key) {
        return new EntityInstanceDescriptor(entityType, key);
    }

    @Override
    public <E> E get(Class<E> entityType, Serializable key) {
        final Object value = read(getDescriptor(entityType, key));
        return value == null ? null : entityType.cast(value);
    }

}
