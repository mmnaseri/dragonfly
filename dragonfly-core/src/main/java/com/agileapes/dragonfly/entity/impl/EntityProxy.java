package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.enhancer.api.MethodDescriptor;
import com.agileapes.couteau.enhancer.api.MethodProxy;
import com.agileapes.dragonfly.cg.SecuredInterfaceInterceptor;
import com.agileapes.dragonfly.data.DataAccessObject;
import com.agileapes.dragonfly.entity.InitializedEntity;
import com.agileapes.dragonfly.security.DataSecurityManager;

import java.io.Serializable;
import java.util.List;

/**
 * This class is the default interceptor used throughout the application to intercept any given
 * entity's dynamic method introduction calls.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 15:36)
 */
public class EntityProxy<E> extends SecuredInterfaceInterceptor implements DataAccessObject<E, Serializable>, InitializedEntity<E> {

    private String token;
    private E originalCopy;

    public EntityProxy(DataSecurityManager securityManager) {
        super(securityManager);
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
        return methodProxy.callSuper(target, arguments);
    }

    @Override
    public void initialize(Class<E> entityType, E entity, String token) {
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
    public boolean isDirtied() {
        return false;
    }

    @Override
    public void freeze() {
    }

    @Override
    public void unfreeze() {
    }
}
