/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.agileapes.dragonfly.entity.impl;

import com.mmnaseri.couteau.basics.api.Filter;
import com.mmnaseri.couteau.enhancer.api.MethodDescriptor;
import com.mmnaseri.couteau.enhancer.api.MethodProxy;
import com.mmnaseri.couteau.reflection.util.ReflectionUtils;
import com.agileapes.dragonfly.cg.SecuredInterfaceInterceptor;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessObject;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.entity.EntityHandler;
import com.agileapes.dragonfly.entity.EntityInitializationContext;
import com.agileapes.dragonfly.entity.InitializedEntity;
import com.agileapes.dragonfly.error.EntityDeletedError;
import com.agileapes.dragonfly.error.NoPrimaryKeyDefinedError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.RelationMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.security.DataSecurityManager;
import com.agileapes.dragonfly.tools.ColumnPropertyFilter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * This class is the default interceptor used throughout the application to intercept any given
 * entity's dynamic method introduction calls.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 15:36)
 */
public class EntityProxy<E> extends SecuredInterfaceInterceptor implements DataAccessObject<E, Serializable>, InitializedEntity<E> {

    private final TableMetadata<E> tableMetadata;
    private final EntityHandler<E> entityHandler;
    private final DataAccess dataAccess;
    private final DataAccessSession session;
    private final EntityContext entityContext;
    private Class<E> entityType;
    private E entity;
    private String token;
    private E originalCopy;
    private EntityInitializationContext initializationContext;
    private final Filter<MethodDescriptor> setterMethodFilter;
    private final Filter<MethodDescriptor> getterMethodFilter;
    private boolean deleted = false;
    private volatile int freezeLock = 0;
    private final Set<String> lazyLoadedProperties;
    private Map<String, Object> map;

    public EntityProxy(DataSecurityManager securityManager, TableMetadata<E> tableMetadata, EntityHandler<E> entityHandler, DataAccess dataAccess, DataAccessSession session, EntityContext entityContext) {
        super(securityManager);
        this.tableMetadata = tableMetadata;
        this.entityHandler = entityHandler;
        this.dataAccess = dataAccess;
        this.session = session;
        this.entityContext = entityContext;
        this.setterMethodFilter = new Filter<MethodDescriptor>() {
            @Override
            public boolean accepts(MethodDescriptor item) {
                return item.getName().matches("set[A-Z].*") && item.getReturnType().equals(void.class) && item.getParameterTypes().length == 1;
            }
        };
        this.getterMethodFilter = new Filter<MethodDescriptor>() {
            @Override
            public boolean accepts(MethodDescriptor item) {
                return item.getName().matches("get[A-Z].*") && !item.getReturnType().equals(void.class) && item.getParameterTypes().length == 0;
            }
        };
        lazyLoadedProperties = new HashSet<String>();
    }

    @Override
    public Object intercept(MethodDescriptor methodDescriptor, Object target, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        if (deleted) {
            throw new EntityDeletedError();
        }
        return super.intercept(methodDescriptor, target, arguments, methodProxy);
    }

    @Override
    public void load() {
        invalidateCachedVersion();
        final E found;
        if (entityHandler.hasKey() && entityHandler.getKey(entity) != null) {
            found = dataAccess.find(entityHandler.getEntityType(), entityHandler.getKey(entity));
        } else {
            final List<E> list = dataAccess.find(entity);
            if (list.isEmpty()) {
                deleted = true;
                throw new EntityDeletedError();
            } else if (list.size() != 1) {
                throw new NoPrimaryKeyDefinedError(entityType);
            } else {
                found = list.get(0);
            }
        }
        freeze();
        entityHandler.copy(found, entity);
        unfreeze();
        setOriginalCopy(found);
    }

    @Override
    public void save() {
        dataAccess.save(entity);
    }

    @Override
    public void delete() {
        dataAccess.delete(entity);
        deleted = true;
    }

    @Override
    public List<E> findLike() {
        return dataAccess.find(entity);
    }

    @Override
    public List<E> query(String queryName) {
        return dataAccess.executeQuery(entity, queryName);
    }

    @Override
    public int update(String queryName) {
        return dataAccess.executeUpdate(entity, queryName);
    }

    @Override
    protected Object call(MethodDescriptor methodDescriptor, Object target, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        if (!isFrozen()) {
            if (initializationContext != null && setterMethodFilter.accepts(methodDescriptor)) {
                final String propertyName = ReflectionUtils.getPropertyName(methodDescriptor.getName());
                final ColumnMetadata columnMetadata = with(tableMetadata.getColumns()).find(new ColumnPropertyFilter(propertyName));
                if (columnMetadata != null) {
                    invalidateCachedVersion();
                }
            }
            if (getterMethodFilter.accepts(methodDescriptor)) {
                final String propertyName = ReflectionUtils.getPropertyName(methodDescriptor.getName());
                if (!lazyLoadedProperties.contains(propertyName)) {
                    lazyLoadedProperties.add(propertyName);
                    final RelationMetadata<E, ?> relationMetadata = with(tableMetadata.getForeignReferences()).find(new Filter<RelationMetadata<E, ?>>() {
                        @Override
                        public boolean accepts(RelationMetadata<E, ?> referenceMetadata) {
                            return propertyName.equals(referenceMetadata.getPropertyName());
                        }
                    });
                    if (relationMetadata != null && relationMetadata.isLazy() && map != null) {
                        freeze();
                        entityHandler.loadLazyRelation(entity, relationMetadata, dataAccess, entityContext, map, session);
                        unfreeze();
                    }
                }
            }
        }
        return methodProxy.callSuper(target, arguments);
    }

    private void invalidateCachedVersion() {
        if (initializationContext != null && !isFrozen()) {
            initializationContext.delete(entityType, entityHandler.getKey(entity));
        }
    }

    @Override
    public void initialize(Class<E> entityType, E entity, String token) {
        this.entityType = entityType;
        this.entity = entity;
        this.token = token;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public synchronized void setOriginalCopy(E originalCopy) {
        this.originalCopy = originalCopy;
    }

    @Override
    public E getOriginalCopy() {
        return originalCopy;
    }

    @Override
    public void freeze() {
        freezeLock ++;
    }

    @Override
    public void unfreeze() {
        freezeLock --;
        if (freezeLock < 0) {
            throw new IllegalStateException();
        }
    }

    @Override
    public void setInitializationContext(EntityInitializationContext initializationContext) {
        this.initializationContext = initializationContext;
    }

    @Override
    public EntityInitializationContext getInitializationContext() {
        return initializationContext;
    }

    @Override
    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public boolean isFrozen() {
        return freezeLock > 0;
    }

    public Class<E> getEntityType() {
        return entityType;
    }

}
