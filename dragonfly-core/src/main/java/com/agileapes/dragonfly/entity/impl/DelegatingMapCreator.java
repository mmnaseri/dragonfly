package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.EntityMapCreator;
import com.agileapes.dragonfly.entity.EntityMapHandler;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.Collection;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 5:33)
 */
public class DelegatingMapCreator implements EntityMapCreator {

    private final EntityMapHandler<Object> handler;

    public DelegatingMapCreator(EntityMapHandler<Object> handler) {
        this.handler = handler;
    }

    @Override
    public <E> Map<String, Object> toMap(TableMetadata<E> tableMetadata, E entity) {
        if (!handler.getEntityType().isInstance(entity)) {
            throw new UnsupportedOperationException();
        }
        return handler.toMap(entity);
    }

    @Override
    public <E> Map<String, Object> toMap(Collection<ColumnMetadata> columns, E entity) {
        if (!handler.getEntityType().isInstance(entity)) {
            throw new UnsupportedOperationException();
        }
        return handler.toMap(entity);
    }

}
