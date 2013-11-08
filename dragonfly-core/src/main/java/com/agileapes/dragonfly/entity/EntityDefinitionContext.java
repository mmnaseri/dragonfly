/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.entity;

import java.util.Collection;

/**
 * This is the main context that holds definitions for all available entities
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 14:14)
 */
public interface EntityDefinitionContext {

    /**
     * Will add an entity definition interceptor. This way, the definitions can be
     * intercepted and modified on the fly as they are added to the context.
     * @param interceptor    the interceptor.
     */
    void addInterceptor(EntityDefinitionInterceptor interceptor);

    /**
     * Adds a new entity definition to the context
     * @param entityDefinition    the definition to be added
     */
    void addDefinition(EntityDefinition<?> entityDefinition);

    /**
     * Returns the definition of an entity of the given type
     * @param entityType    the type of the entity to look up
     * @param <E>           the type parameter for the entity
     * @return the definition of the given entity type
     */
    <E> EntityDefinition<E> getDefinition(Class<E> entityType);

    /**
     * @return all the definitions registered with the context
     */
    Collection<EntityDefinition<?>> getDefinitions();

    /**
     * @return all the different entity types registered with the context
     */
    Collection<Class<?>> getEntities();

}
