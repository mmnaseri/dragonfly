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

package com.agileapes.dragonfly.events;

import com.mmnaseri.couteau.basics.api.Processor;
import com.mmnaseri.couteau.reflection.beans.BeanInitializer;
import com.mmnaseri.couteau.reflection.beans.impl.ConstructorBeanInitializer;
import com.mmnaseri.couteau.reflection.error.BeanInstantiationException;
import com.mmnaseri.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.agileapes.dragonfly.events.impl.AbstractDataAccessEventHandler;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import static com.mmnaseri.couteau.reflection.util.ReflectionUtils.withMethods;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/11, 12:37)
 */
@SuppressWarnings("unchecked")
public class EntityEventCallback extends AbstractDataAccessEventHandler {

    private final Class<?> callbackClass;
    private final Class<?> entityClass;
    private final BeanInitializer initializer;

    public EntityEventCallback() {
        this(null, null);
    }

    public EntityEventCallback(Class<?> entityClass, Class<?> callbackClass) {
        this.entityClass = entityClass;
        this.callbackClass = callbackClass;
        this.initializer = new ConstructorBeanInitializer();
    }

    private Object getCallback(Object entity) {
        if (callbackClass == null || entityClass == null) {
            return entity;
        }
        try {
            return initializer.initialize(callbackClass, new Class[0]);
        } catch (BeanInstantiationException e) {
            throw new IllegalArgumentException("Failed to initialize listener callback", e);
        }
    }

    private <E> void runCallback(final E entity, Class<? extends Annotation> event) {
        if (entityClass != null && !entityClass.isInstance(entity)) {
            return;
        }
        final Object callback = getCallback(entity);
        withMethods(callback.getClass())
                .keep(new AnnotatedElementFilter(event))
                .each(new Processor<Method>() {
                    @Override
                    public void process(Method method) {
                        try {
                            method.setAccessible(true);
                            if (method.getParameterTypes().length == 0) {
                                method.invoke(callback);
                            } else if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isInstance(entity)) {
                                method.invoke(callback, entity);
                            } else {
                                throw new IllegalStateException("Callback method not supported: " + method);
                            }
                        } catch (Exception e) {
                            throw new IllegalStateException("Failed to execute callback: " + method, e);
                        }
                    }
                });
    }

    @Override
    public <E> void beforeInsert(final E entity) {
        runCallback(entity, PrePersist.class);
    }

    @Override
    public <E> void afterInsert(E entity) {
        runCallback(entity, PostPersist.class);
    }

    @Override
    public <E> void beforeUpdate(E entity) {
        runCallback(entity, PreUpdate.class);
    }

    @Override
    public <E> void afterUpdate(E entity, boolean updated) {
        runCallback(entity, PostUpdate.class);
    }

    @Override
    public <E> void beforeDelete(E entity) {
        runCallback(entity, PreRemove.class);
    }

    @Override
    public <E> void afterDelete(E entity) {
        runCallback(entity, PostRemove.class);
    }

    @Override
    public <E> void afterFind(E sample, List<E> entities) {
        for (E entity : entities) {
            runCallback(entity, PostLoad.class);
        }
    }

}
