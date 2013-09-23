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
