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

package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.context.contract.Registry;
import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.couteau.context.impl.ConcurrentRegistry;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.Collection;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/30, 14:42)
 */
public class DefaultTableMetadataRegistry implements TableMetadataRegistry {

    private final Registry<TableMetadata<?>> registry = new ConcurrentRegistry<TableMetadata<?>>();
    private final Transformer<Class<?>, String> mapper = new Transformer<Class<?>, String>() {
        @Override
        public String map(Class<?> aClass) {
            return aClass.getCanonicalName();
        }
    };
    private Processor<TableMetadataRegistry> registryProcessor;

    @Override
    public Collection<Class<?>> getEntityTypes() {
        return with(registry.getBeans()).transform(new Transformer<TableMetadata<?>, Class<?>>() {
            @Override
            public Class<?> map(TableMetadata<?> tableMetadata) {
                return tableMetadata.getEntityType();
            }
        }).list();
    }

    @Override
    public <E> TableMetadata<E> getTableMetadata(Class<E> entityType) {
        final String key = mapper.map(entityType);
        try {
            //noinspection unchecked
            return registry.contains(key) ? (TableMetadata<E>) registry.get(key) : null;
        } catch (RegistryException e) {
            return null;
        }
    }

    @Override
    public synchronized <E> void addTableMetadata(TableMetadata<E> tableMetadata) {
        final String key = mapper.map(tableMetadata.getEntityType());
        try {
            registry.register(key, tableMetadata);
        } catch (RegistryException ignored) {
        }
        if (registryProcessor != null) {
            registryProcessor.process(this);
        }
    }

    @Override
    public synchronized boolean contains(Class<?> entityType) {
        return registry.contains(mapper.map(entityType));
    }

    @Override
    public void setChangeCallback(Processor<TableMetadataRegistry> registryProcessor) {
        this.registryProcessor = registryProcessor;
    }

    protected void addInternalMetadata(TableMetadata<?> tableMetadata) {
        try {
            registry.register(mapper.map(tableMetadata.getEntityType()), tableMetadata);
        } catch (RegistryException e) {
            throw new IllegalStateException("More than one internal table has been registered for the given type: " + tableMetadata.getEntityType());
        }
    }

}
