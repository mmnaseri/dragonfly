package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.EntityFactory;
import com.agileapes.dragonfly.error.EntityInitializationError;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/15, 14:36)
 */
public class GenericEntityFactory<E> implements EntityFactory<E> {

    private final Class<E> entityType;

    public GenericEntityFactory(Class<E> entityType) {
        this.entityType = entityType;
    }

    @Override
    public E getInstance() {
        try {
            return entityType.newInstance();
        } catch (Exception e) {
            throw new EntityInitializationError(entityType, e);
        }
    }

}
