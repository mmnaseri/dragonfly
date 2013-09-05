package com.agileapes.dragonfly.metadata;

import com.agileapes.couteau.basics.api.Processor;

import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/30, 14:35)
 */
public interface MetadataRegistry {

    Collection<Class<?>> getEntityTypes();

    <E> TableMetadata<E> getTableMetadata(Class<E> entityType);

    <E> void addTableMetadata(TableMetadata<E> tableMetadata);

    boolean contains(Class<?> entityType);

    void setChangeCallback(Processor<MetadataRegistry> registryProcessor);

}
