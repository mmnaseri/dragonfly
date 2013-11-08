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

package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.EntityDefinition;

import java.util.Map;

/**
 * This is an immutable entity definition class.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 14:17)
 */
public class ImmutableEntityDefinition<E> implements EntityDefinition<E> {

    private final Class<E> entityType;
    private Map<Class<?>, Class<?>> interfaces;

    public ImmutableEntityDefinition(Class<E> entityType, Map<Class<?>, Class<?>> interfaces) {
        this.entityType = entityType;
        this.interfaces = interfaces;
    }

    @Override
    public Class<E> getEntityType() {
        return entityType;
    }

    @Override
    public Map<Class<?>, Class<?>> getInterfaces() {
        return interfaces;
    }
}
