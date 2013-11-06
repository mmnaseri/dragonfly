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
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/20, 16:37)
 */
public interface EntityInitializationContext {

    <E> void delete(Class<E> entityType, Serializable key);

    <E> void register(Class<E> entityType, Serializable key, E entity);

    <E> E get(Class<E> entityType, Serializable key);

    <E> E get(Class<E> entityType, Serializable key, Class<?> requestingEntityType, Serializable requesterKey);

    void lock();

    void unlock();

    <E> boolean contains(Class<E> entityType, Serializable key);

    DataAccess getDataAccess();

}
