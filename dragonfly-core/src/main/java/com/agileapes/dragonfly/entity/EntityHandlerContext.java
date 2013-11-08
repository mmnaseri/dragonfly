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

/**
 * This is a context that facilitates interaction with and operation on different
 * entities through entity handlers.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 5:31)
 */
public interface EntityHandlerContext extends EntityMapCreator, MapEntityCreator {

    /**
     * Adds a handler to the context. Handlers are expected to uniquely apply to a given
     * entity type
     * @param handler    the entity handler to be added
     */
    void addHandler(EntityHandler<?> handler);

    /**
     * Returns the handler for the given entity type
     * @param entityType    the type of the entity
     * @param <E>           the type parameter for the entity
     * @return the handler
     */
    <E> EntityHandler<E> getHandler(Class<E> entityType);

    /**
     * Returns the handler for the given entity
     * @param entity        the entity
     * @param <E>           the type parameter for the entity
     * @return the handler
     */
    <E> EntityHandler<E> getHandler(E entity);
}
