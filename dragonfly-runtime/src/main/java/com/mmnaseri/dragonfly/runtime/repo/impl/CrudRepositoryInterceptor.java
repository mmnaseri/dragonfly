/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
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

package com.mmnaseri.dragonfly.runtime.repo.impl;

import com.mmnaseri.dragonfly.data.DataAccess;
import com.mmnaseri.dragonfly.entity.EntityHandlerContext;
import com.mmnaseri.dragonfly.runtime.repo.CrudRepository;
import com.mmnaseri.dragonfly.runtime.repo.MethodInterceptionStrategy;
import com.mmnaseri.dragonfly.runtime.repo.impl.strategies.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/14 AD, 12:22)
 */
public class CrudRepositoryInterceptor implements MethodInterceptor, CrudRepository<Object, Serializable> {

    private final BeanFactory beanFactory;
    private final Class repository;
    private final Class entityType;
    private final Class keyType;
    private DataAccess dataAccess;
    private List<MethodInterceptionStrategy> strategies = null;
    private EntityHandlerContext entityHandlerContext;

    public CrudRepositoryInterceptor(BeanFactory beanFactory, Class repository, Class entityType, Class keyType) {
        this.beanFactory = beanFactory;
        this.repository = repository;
        this.entityType = entityType;
        this.keyType = keyType;
    }

    private DataAccess getDataAccess() {
        if (dataAccess == null) {
            dataAccess = beanFactory.getBean(DataAccess.class);
        }
        return dataAccess;
    }

    private EntityHandlerContext getEntityHandlerContext() {
        if (entityHandlerContext == null) {
            entityHandlerContext = beanFactory.getBean(EntityHandlerContext.class);
        }
        return entityHandlerContext;
    }

    private synchronized void initializeStrategies() {
        if (strategies != null) {
            return;
        }
        strategies = new ArrayList<MethodInterceptionStrategy>();
        /**
         * `.toString()` should result in a proper name
         */
        strategies.add(new CrudRepositoryToStringMethodInterceptionStrategy(repository, entityType, keyType));
        /**
         * Any method declared by CrudRepository and implemented here must be delegated to this class
         */
        strategies.add(new InterfaceMethodInterceptionStrategy(CrudRepository.class, this));
        /**
         * Any method annotated with @NativeQuery that returns an instance of the entity or a list or returns `void`
         */
        strategies.add(new NamedNativeQueryMethodInterceptionStrategy(entityType, getDataAccess(), getEntityHandlerContext().getHandler(entityType)));
        /**
         * Any method annotated with @QueryAlias
         */
        strategies.add(new QueryAliasMethodInterceptionStrategy(entityType, getDataAccess(), getEntityHandlerContext().getHandler(entityType)));
        /**
         * Everything starting with `findBy` must be delegated to a parser strategy capable of constructing a sample object
         */
        strategies.add(new FindBySampleMethodInterceptionStrategy(entityType, getDataAccess()));
        /**
         * Everything starting with `deleteBy` must be delegated to something that constructs a sample from the arguments
         */
        strategies.add(new DeleteBySampleMethodInterceptionStrategy(entityType, getDataAccess()));
    }

    private Object checkEntity(Object entity) {
        if (entity == null) {
            throw new NullPointerException("Entity cannot be null");
        }
        if (!entityType.isInstance(entity)) {
            throw new IllegalArgumentException("Entity must be an instance of " + entityType.getCanonicalName());
        }
        return entity;
    }

    private Serializable checkKey(Serializable key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        if (!keyType.isInstance(key)) {
            throw new IllegalArgumentException("Key must be an instance of " + keyType.getCanonicalName());
        }
        return key;
    }

    @Override
    public Object intercept(Object target, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        initializeStrategies();
        for (MethodInterceptionStrategy strategy : strategies) {
            if (strategy.accepts(method)) {
                return strategy.intercept(target, method, arguments, methodProxy);
            }
        }
        throw new UnsupportedOperationException("Operation " + method.getName() + " is not supported for " + entityType.getCanonicalName());
    }

    @Override
    public Object save(Object entity) {
        return getDataAccess().save(checkEntity(entity));
    }

    @Override
    public Object insert(Object entity) {
        return getDataAccess().insert(checkEntity(entity));
    }

    @Override
    public Object update(Object entity) {
        return getDataAccess().update(checkEntity(entity));
    }

    @Override
    public void delete(Object entity) {
        getDataAccess().delete(checkEntity(entity));
    }

    @Override
    public void deleteOne(Serializable key) {
        getDataAccess().delete(entityType, checkKey(key));
    }

    @Override
    public void deleteAll() {
        getDataAccess().deleteAll(entityType);
    }

    @Override
    public Object findOne(Serializable key) {
        return getDataAccess().find(entityType, checkKey(key));
    }

    @Override
    public List<Object> findAll() {
        //noinspection unchecked
        return getDataAccess().findAll(entityType);
    }

    @Override
    public List<Object> findLike(Object sample) {
        return getDataAccess().find(sample);
    }

}
