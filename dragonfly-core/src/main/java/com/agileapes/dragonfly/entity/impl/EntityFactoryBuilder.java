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

package com.agileapes.dragonfly.entity.impl;

import com.mmnaseri.couteau.reflection.cp.MappedClassLoader;
import com.agileapes.dragonfly.entity.EntityFactory;
import com.agileapes.dragonfly.error.EntityInitializationError;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class will try to load an entity factory from the classpath first, and then if not available,
 * instantiate a generic factory. This way, if the Maven plugin has been used, or entity factories
 * have been compiled with the project, that class will take precedence over the more generic,
 * common-purpose one available with the framework.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 14:25)
 */
public class EntityFactoryBuilder<E> {

    private final Class<E> entityType;
    private final Class<? extends E> enhancedType;

    public EntityFactoryBuilder(Class<E> entityType, Class<? extends E> enhancedType) {
        this.entityType = entityType;
        this.enhancedType = enhancedType;
    }

    public EntityFactory<E> getEntityFactory() {
        final String factoryName = entityType.getCanonicalName() + "EntityFactory";
        final String factoryPath = factoryName.replace('.', File.separatorChar).concat(".class");
        final MappedClassLoader classLoader = new MappedClassLoader(enhancedType.getClassLoader());
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(factoryPath);
        if (inputStream == null) {
            //the maven plugin has not been used
            //to generate factories
            //noinspection unchecked
            return new GenericEntityFactory<E>((Class<E>) enhancedType);
        }
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int read;
        try {
            while ((read = inputStream.read()) != -1) {
                outputStream.write(read);
            }
        } catch (IOException e) {
            throw new EntityInitializationError(entityType, e);
        }
        classLoader.register(factoryName, outputStream.toByteArray());
        final Class<?> factoryClass;
        final Object instance;
        try {
            factoryClass = Class.forName(factoryName, false, classLoader);
            instance = factoryClass.newInstance();
        } catch (Exception e) {
            throw new EntityInitializationError(entityType, e);
        }
        //noinspection unchecked
        return (EntityFactory<E>) instance;
    }

}
