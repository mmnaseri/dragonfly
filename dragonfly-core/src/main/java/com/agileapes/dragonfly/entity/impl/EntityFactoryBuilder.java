package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.reflection.cp.MappedClassLoader;
import com.agileapes.dragonfly.entity.EntityFactory;
import com.agileapes.dragonfly.error.EntityInitializationError;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
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
