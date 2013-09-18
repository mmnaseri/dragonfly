package com.agileapes.dragonfly.entity;

import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 14:12)
 */
public interface EntityDefinition<E> {

    Class<E> getEntityType();

    Map<Class<?>, Class<?>> getInterfaces();

}
