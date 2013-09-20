package com.agileapes.dragonfly.entity;

import java.io.Serializable;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/20, 16:37)
 */
public interface EntityInitializationContext {

    <E> void register(Class<E> entityType, Serializable key, E entity);

    <E> E get(Class<E> entityType, Serializable key);

}
