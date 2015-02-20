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

package com.agileapes.dragonfly.entity;

import java.util.Map;

/**
 * This interface builds on the {@link EntityContext}'s initialization abilities to allow for
 * dynamic specification of new interfaces to be assigned to the dispensed entities as they
 * are generated anew.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 21:24)
 */
public interface ModifiableEntityContext extends EntityContext {

    /**
     * Changes the interfaces initially designated for entities of each given type.
     * Each entry has the entity type as the key and a map of key-values containing
     * interfaces and their concrete implementations as values.
     * @param interfaces    the interfaces for the entities
     */
    void setInterfaces(Map<Class<?>, Map<Class<?>, Class<?>>> interfaces);

    /**
     * Sets the entity factories for different types
     * @param factories    a map of entity types to entity factories for entity types
     */
    void setEntityFactories(Map<Class<?>, EntityFactory<?>> factories);

    /**
     * Changes the default class loader for the entity context. This will be used for enhancing new class
     * instances and will help look up dependency classes.
     * @param classLoader    the class loader to use.
     */
    void setDefaultClassLoader(ClassLoader classLoader);

}
