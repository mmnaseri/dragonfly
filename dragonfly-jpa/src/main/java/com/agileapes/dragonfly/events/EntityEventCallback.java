package com.agileapes.dragonfly.events;

import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.reflection.beans.BeanInitializer;
import com.agileapes.couteau.reflection.beans.impl.ConstructorBeanInitializer;
import com.agileapes.couteau.reflection.error.BeanInstantiationException;
import com.agileapes.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.agileapes.dragonfly.events.impl.AbstractDataAccessEventHandler;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import static com.agileapes.couteau.reflection.util.ReflectionUtils.withMethods;

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
