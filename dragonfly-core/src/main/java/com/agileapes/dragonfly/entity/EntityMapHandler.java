package com.agileapes.dragonfly.entity;

import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 4:49)
 */
public interface EntityMapHandler<E> {

    Class<E> getEntityType();

    Map<String, Object> toMap(E entity);

    E fromMap(E entity, Map<String, Object> map);

}
