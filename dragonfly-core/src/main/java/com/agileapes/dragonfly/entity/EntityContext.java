package com.agileapes.dragonfly.entity;

import com.agileapes.dragonfly.metadata.TableMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 15:11)
 */
public interface EntityContext {

    <E> E getInstance(Class<E> entityType);

    <E> E getInstance(TableMetadata<E> tableMetadata);

    <E> boolean has(E entity);

}
