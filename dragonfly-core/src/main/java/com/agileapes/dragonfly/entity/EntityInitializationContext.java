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

import com.agileapes.dragonfly.data.DataAccess;

import java.io.Serializable;

/**
 * <p>This is a context for initialized entities. It also caches entities so that redundant fetch
 * operations are reduced to a minimum.</p>
 *
 * <p>The main short-coming of the context is that it does not know how to cache entities
 * that do not define a key.</p>
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/20, 16:37)
 */
public interface EntityInitializationContext {

    /**
     * Un-registers the entity with the given key from the context if it has been registered
     * @param entityType    the type of the entity
     * @param key           the key to the entity
     * @param <E>           the type parameter for the entity
     */
    <E> void delete(Class<E> entityType, Serializable key);

    /**
     * Registers the entity with the given type and key with the context
     * @param entityType    the type of the entity
     * @param key           the key to the entity
     * @param entity        the actual instance of the entity with the type and key provided
     * @param <E>           the type parameter for the entity
     */
    <E> void register(Class<E> entityType, Serializable key, E entity);

    /**
     * Returns the cached instance of the entity with the key or a freshly retrieved one from the
     * bound data access instance
     * @param entityType    the type of the entity
     * @param key           the key to the entity
     * @param <E>           the type parameter for the entity
     * @return an instance of the desired entity
     */
    <E> E get(Class<E> entityType, Serializable key);

    /**
     * Returns the cached instance of the entity with the key or a freshly retrieved one from the
     * bound data access instance. This method will help with keeping track of inter-entity
     * associations, so that they can be invalidated together.
     * @param entityType                the type of the entity
     * @param key                       the key to the entity
     * @param requestingEntityType      the type of the entity requesting an instance of the given entity type
     * @param requesterKey              the key for the requesting entity type
     * @param <E>                       the type parameter for the entity
     * @return an instance of the desired entity
     */
    <E> E get(Class<E> entityType, Serializable key, Class<?> requestingEntityType, Serializable requesterKey);

    /**
     * Locks the context so that no entity instances can be removed from the context
     */
    void lock();

    /**
     * Unlocks the context
     * @see #lock()
     */
    void unlock();

    /**
     * Determines whether an instance of the given entity type with the provided key has been
     * cached within the context or not
     * @param entityType    the type of the entity
     * @param key           the key to the entity
     * @param <E>           the type parameter for the entity
     * @return {@code true} if an instance of the entity is accessible within the context
     */
    <E> boolean contains(Class<E> entityType, Serializable key);

    /**
     * @return the data access instance bound to this initialization context
     */
    DataAccess getDataAccess();

    /**
     * Removes all entities associated with the given type from the context
     * @param entityType    the type of the entity
     * @param <E>           the type parameter for the entity
     */
    <E> void delete(Class<E> entityType);
}
