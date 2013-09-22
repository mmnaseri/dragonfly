package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.enhancer.api.MethodDescriptor;
import com.agileapes.couteau.enhancer.api.MethodProxy;
import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.dragonfly.cg.SecuredInterfaceInterceptor;
import com.agileapes.dragonfly.data.DataAccessObject;
import com.agileapes.dragonfly.entity.EntityHandler;
import com.agileapes.dragonfly.entity.EntityInitializationContext;
import com.agileapes.dragonfly.entity.InitializedEntity;
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
    private Class<E> entityType;
    private E entity;
    private String token;
    private E originalCopy;
    private EntityInitializationContext initializationContext;
    private final Filter<MethodDescriptor> setterMethodFilter;

    public EntityProxy(DataSecurityManager securityManager, TableMetadata<E> tableMetadata, EntityHandler<E> entityHandler) {
        super(securityManager);
        this.tableMetadata = tableMetadata;
        this.entityHandler = entityHandler;
        this.setterMethodFilter = new Filter<MethodDescriptor>() {
            @Override
            public boolean accepts(MethodDescriptor item) {
                return item.getName().matches("set[A-Z].*") && item.getReturnType().equals(void.class) && item.getParameterTypes().length == 1;
            }
        };
    }

    @Override
    public Object intercept(MethodDescriptor methodDescriptor, Object target, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        return super.intercept(methodDescriptor, target, arguments, methodProxy);
    }

    @Override
    public void load() {
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
    }

    @Override
    public List<E> findLike() {
        return null;
    }

    @Override
    public List<E> query(String queryName) {
        return null;
    }

    @Override
    public int update(String queryName) {
        return 0;
    }

    @Override
    protected Object call(MethodDescriptor methodDescriptor, Object target, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        if (initializationContext != null && setterMethodFilter.accepts(methodDescriptor)) {
            final String propertyName = ReflectionUtils.getPropertyName(methodDescriptor.getName());
            final ColumnMetadata columnMetadata = with(tableMetadata.getColumns()).find(new ColumnPropertyFilter(propertyName));
            if (columnMetadata != null) {
                initializationContext.delete(entityType, entityHandler.getKey(entity));
            }
        }
        return methodProxy.callSuper(target, arguments);
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
