package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.*;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 5:38)
 */
public class DefaultEntityMapHandlerContext implements EntityMapHandlerContext {
    
    private final Map<Class<?>, EntityMapCreator> mapCreators;
    private final Map<Class<?>, MapEntityCreator> entityCreators;
    private final EntityMapCreator defaultMapCreator;
    private final MapEntityCreator defaultEntityCreator;
    private final EntityContext entityContext;

    public DefaultEntityMapHandlerContext(EntityContext entityContext) {
        this.entityContext = entityContext;
        defaultEntityCreator = new DefaultMapEntityCreator(this.entityContext);
        defaultMapCreator = new DefaultEntityMapCreator();
        entityCreators = new ConcurrentHashMap<Class<?>, MapEntityCreator>();
        mapCreators = new ConcurrentHashMap<Class<?>, EntityMapCreator>();
    }

    @Override
    public void addMapHandler(EntityMapHandler<?> mapHandler) {
        //noinspection unchecked
        entityCreators.put(mapHandler.getEntityType(), new DelegatingEntityCreator(entityContext, (EntityMapHandler<Object>) mapHandler));
        //noinspection unchecked
        mapCreators.put(mapHandler.getEntityType(), new DelegatingMapCreator((EntityMapHandler<Object>) mapHandler));
    }

    public EntityMapCreator getMapCreator(Class<?> entityType) {
        if (mapCreators.containsKey(entityType)) {
            return mapCreators.get(entityType);
        }
        return defaultMapCreator;
    }

    public MapEntityCreator getEntityCreator(Class<?> entityType) {
        if (entityCreators.containsKey(entityType)) {
            return entityCreators.get(entityType);
        }
        return defaultEntityCreator;
    }

    @Override
    public <E> Map<String, Object> toMap(TableMetadata<E> tableMetadata, E entity) {
        return getMapCreator(tableMetadata.getEntityType()).toMap(tableMetadata, entity);
    }

    @Override
    public <E> Map<String, Object> toMap(Collection<ColumnMetadata> columns, E entity) {
        return getMapCreator(entity.getClass()).toMap(columns, entity);
    }

    @Override
    public <E> E fromMap(TableMetadata<E> tableMetadata, Map<String, Object> values) {
        return getEntityCreator(tableMetadata.getEntityType()).fromMap(tableMetadata, values);
    }

    @Override
    public <E> E fromMap(E entity, Collection<ColumnMetadata> columns, Map<String, Object> values) {
        return getEntityCreator(entity.getClass()).fromMap(entity, columns, values);
    }
    
}
