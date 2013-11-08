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

import com.agileapes.dragonfly.entity.impl.EntityProxy;

/**
 * The entity factory allows for dynamic instantiation of entities and therefore will allow
 * for instantiation of enhanced entity classes. Each factory instance is singly bound to a
 * predesignated entity type, exactly one factory instance is expected to exist for each
 * entity type.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 14:21)
 */
public interface EntityFactory<E> {

    /**
     * Returns a new instance of the entity bound to the given type.
     * @param proxy    the proxy to intercept calls to the methods of the proxied entity
     * @return the enhanced instance of the given entity.
     */
    E getInstance(EntityProxy<E> proxy);

}
