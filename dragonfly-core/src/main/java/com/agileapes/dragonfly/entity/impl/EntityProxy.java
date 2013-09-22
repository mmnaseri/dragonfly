package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.enhancer.api.MethodDescriptor;
import com.agileapes.couteau.enhancer.api.MethodProxy;
import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.dragonfly.cg.SecuredInterfaceInterceptor;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessObject;
import com.agileapes.dragonfly.entity.EntityHandler;
import com.agileapes.dragonfly.entity.EntityInitializationContext;
import com.agileapes.dragonfly.entity.InitializedEntity;
import com.agileapes.dragonfly.error.EntityDeletedError;
import com.agileapes.dragonfly.error.NoPrimaryKeyDefinedError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.security.DataSecurityManager;
import com.agileapes.dragonfly.tools.ColumnPropertyFilter;

import java.io.Serializable;
import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

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
    private Class<E> entityType;
    private E entity;
    private String token;
    private E originalCopy;
    private EntityInitializationContext initializationContext;
    private final Filter<MethodDescriptor> setterMethodFilter;
    private boolean deleted = false;

    public EntityProxy(DataSecurityManager securityManager, TableMetadata<E> tableMetadata, EntityHandler<E> entityHandler, DataAccess dataAccess) {
        super(securityManager);
        this.tableMetadata = tableMetadata;
        this.entityHandler = entityHandler;
        this.dataAccess = dataAccess;
        this.setterMethodFilter = new Filter<MethodDescriptor>() {
            @Override
            public boolean accepts(MethodDescriptor item) {
                return item.getName().matches("set[A-Z].*") && item.getReturnType().equals(void.class) && item.getParameterTypes().length == 1;
            }
        };
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
        this.entity = found;
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
        if (initializationContext != null && setterMethodFilter.accepts(methodDescriptor)) {
            final String propertyName = ReflectionUtils.getPropertyName(methodDescriptor.getName());
            final ColumnMetadata columnMetadata = with(tableMetadata.getColumns()).find(new ColumnPropertyFilter(propertyName));
            if (columnMetadata != null) {
                invalidateCachedVersion();
            }
        }
        return methodProxy.callSuper(target, arguments);
    }

    private void invalidateCachedVersion() {
        if (initializationContext != null) {
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
    public void setOriginalCopy(E originalCopy) {
        this.originalCopy = originalCopy;
    }

    @Override
    public E getOriginalCopy() {
        return originalCopy;
    }

    @Override
    public void freeze() {
    }

    @Override
    public void unfreeze() {
    }

    @Override
    public void setInitializationContext(EntityInitializationContext initializationContext) {
        this.initializationContext = initializationContext;
    }

    @Override
    public EntityInitializationContext getInitializationContext() {
        return initializationContext;
    }

}
