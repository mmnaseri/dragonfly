package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.entity.EntityHandler;
import com.agileapes.dragonfly.entity.MapEntityCreator;
import com.agileapes.dragonfly.error.EntityInitializationError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.Collection;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 5:35)
 */
public class DelegatingEntityCreator implements MapEntityCreator {
    
    private final EntityHandler<Object> handler;
    private final EntityContext entityContext;

    public DelegatingEntityCreator(EntityContext entityContext, EntityHandler<Object> handler) {
        this.handler = handler;
        this.entityContext = entityContext;
    }

    @Override
    public <E> E fromMap(TableMetadata<E> tableMetadata, Map<String, Object> values) {
        try {
            return fromMap(entityContext.getInstance(tableMetadata.getEntityType()), tableMetadata.getColumns(), values);
        } catch (Exception e) {
            throw new EntityInitializationError(tableMetadata.getEntityType(), e);
        }
    }

    @Override
    public <E> E fromMap(E entity, Collection<ColumnMetadata> columns, Map<String, Object> values) {
        if (!handler.getEntityType().isInstance(entity)) {
            throw new UnsupportedOperationException();
        }
        //noinspection unchecked
        return (E) handler.fromMap(entity, values);
    }
}
