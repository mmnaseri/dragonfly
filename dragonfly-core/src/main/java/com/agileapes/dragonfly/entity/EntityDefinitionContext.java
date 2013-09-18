package com.agileapes.dragonfly.entity;

import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 14:14)
 */
public interface EntityDefinitionContext {

    void addInterceptor(EntityDefinitionInterceptor interceptor);

    void addDefinition(EntityDefinition<?> entityDefinition);

    <E> EntityDefinition<E> getDefinition(Class<E> entityType);

    Collection<EntityDefinition<?>> getDefinitions();

    Collection<Class<?>> getEntities();

}
