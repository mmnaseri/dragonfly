package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.*;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 5:38)
 */
public class DefaultEntityHandlerContext implements EntityHandlerContext {
    
    private final Map<Class<?>, EntityMapCreator> mapCreators;
    private final Map<Class<?>, MapEntityCreator> entityCreators;
    private final EntityMapCreator defaultMapCreator;
    private final MapEntityCreator defaultEntityCreator;
    private final EntityContext entityContext;
    private final Map<Class<?>, EntityHandler<?>> entityHandlers;
    private final MetadataRegistry metadataRegistry;

    public DefaultEntityHandlerContext(EntityContext entityContext, MetadataRegistry metadataRegistry) {
        this.entityContext = entityContext;
        this.metadataRegistry = metadataRegistry;
        if (entityContext instanceof DefaultEntityContext) {
            final DefaultEntityContext context = (DefaultEntityContext) entityContext;
            context.setHandlerContext(this);
        }
        defaultEntityCreator = new DefaultMapEntityCreator(this.entityContext);
        defaultMapCreator = new DefaultEntityMapCreator();
        entityCreators = new ConcurrentHashMap<Class<?>, MapEntityCreator>();
        mapCreators = new ConcurrentHashMap<Class<?>, EntityMapCreator>();
        entityHandlers = new ConcurrentHashMap<Class<?>, EntityHandler<?>>();
    }

    @Override
    public void addHandler(EntityHandler<?> entityHandler) {
        //noinspection unchecked
        entityCreators.put(entityHandler.getEntityType(), new DelegatingEntityCreator(entityContext, (EntityHandler<Object>) entityHandler));
        //noinspection unchecked
        mapCreators.put(entityHandler.getEntityType(), new DelegatingMapCreator((EntityHandler<Object>) entityHandler));
        entityHandlers.put(entityHandler.getEntityType(), entityHandler);
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
    
    @Override
    public <E> EntityHandler<E> getHandler(Class<E> entityType) {
        if (entityHandlers.containsKey(entityType)) {
            //noinspection unchecked
            return (EntityHandler<E>) entityHandlers.get(entityType);
        }
        final GenericEntityHandler<E> entityHandler = new GenericEntityHandler<E>(entityType, entityContext, metadataRegistry.getTableMetadata(entityType));
        entityHandlers.put(entityType, entityHandler);
        return entityHandler;
    }
    
}
