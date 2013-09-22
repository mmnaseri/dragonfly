package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.basics.api.impl.CachingDataDispenser;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.entity.EntityInitializationContext;
import com.agileapes.dragonfly.error.ContextLockFailureError;

import java.io.Serializable;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/22, 13:04)
 */
public abstract class AbstractLockingEntityInitializationContext extends CachingDataDispenser<AbstractLockingEntityInitializationContext.EntityInstanceDescriptor, Object> implements EntityInitializationContext {

    public static class EntityInstanceDescriptor {

        private final Class<?> entityType;
        private final Serializable key;

        public EntityInstanceDescriptor(Class<?> entityType, Serializable key) {
            this.entityType = entityType;
            this.key = key;
        }

        public Class<?> getEntityType() {
            return entityType;
        }

        public Serializable getKey() {
            return key;
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

    private final DataAccess dataAccess;
    private final EntityInitializationContext parent;
    private final Object lock = new Object();
    private Integer lockIndex = 0;

    public AbstractLockingEntityInitializationContext(DataAccess dataAccess, EntityInitializationContext parent) {
        this.dataAccess = dataAccess;
        this.parent = parent;
    }

    @Override
    protected Object produce(EntityInstanceDescriptor key) {
        if (parent != null) {
            return parent.get(key.getEntityType(), key.getKey());
        }
        return dataAccess.find(key.getEntityType(), key.getKey());
    }

    @Override
    public <E> void delete(Class<E> entityType, Serializable key) {
        synchronized (lock) {
            //no one is allowed to steal from the context while it is locked ;-)
            //this is to protect the context from being invalidated by accident
            //while it is being populated through the data access interface
            if (lockIndex > 0) {
                return;
            }
        }
        final EntityInstanceDescriptor descriptor = new EntityInstanceDescriptor(entityType, key);
        if (parent != null && !contains(descriptor)) {
            parent.delete(entityType, key);
            return;
        }
        remove(descriptor);
    }

    @Override
    public <E> void register(Class<E> entityType, Serializable key, E entity) {
        if (parent != null) {
            parent.register(entityType, key, entity);
            return;
        }
        write(new EntityInstanceDescriptor(entityType, key), entity);
    }

    @Override
    public <E> E get(Class<E> entityType, Serializable key) {
        final Object value = read(new EntityInstanceDescriptor(entityType, key));
        return value == null ? null : entityType.cast(value);
    }

    @Override
    public void lock() {
        synchronized (lock) {
            lockIndex ++;
        }
    }

    @Override
    public void unlock() {
        synchronized (lock) {
            lockIndex --;
            if (lockIndex < 0) {
                throw new ContextLockFailureError();
            }
        }
    }

    @Override
    public <E> boolean contains(Class<E> entityType, Serializable key) {
        return contains(new EntityInstanceDescriptor(entityType, key));
    }

    @Override
    public DataAccess getDataAccess() {
        return dataAccess;
    }

}
