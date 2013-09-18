package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.EntityDefinition;

import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 14:17)
 */
public class ImmutableEntityDefinition<E> implements EntityDefinition<E> {

    private final Class<E> entityType;
    private Map<Class<?>, Class<?>> interfaces;

    public ImmutableEntityDefinition(Class<E> entityType, Map<Class<?>, Class<?>> interfaces) {
        this.entityType = entityType;
        this.interfaces = interfaces;
    }

    @Override
    public Class<E> getEntityType() {
        return entityType;
    }

    @Override
    public Map<Class<?>, Class<?>> getInterfaces() {
        return interfaces;
    }
}
