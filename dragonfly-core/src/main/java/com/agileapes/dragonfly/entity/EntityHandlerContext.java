package com.agileapes.dragonfly.entity;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 5:31)
 */
public interface EntityHandlerContext extends EntityMapCreator, MapEntityCreator {

    void addHandler(EntityHandler<?> handler);

    <E> EntityHandler<E> getHandler(Class<E> entityType);

    <E> EntityHandler<E> getHandler(E entity);
}
