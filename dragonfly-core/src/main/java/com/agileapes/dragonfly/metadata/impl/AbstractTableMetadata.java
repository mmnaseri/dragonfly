package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.TableMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:47)
 */
public abstract class AbstractTableMetadata<E> implements TableMetadata<E> {

    private final Class<E> entityType;

    public AbstractTableMetadata(Class<E> entityType) {
        this.entityType = entityType;
    }

    @Override
    public Class<E> getEntityType() {
        return entityType;
    }

}
