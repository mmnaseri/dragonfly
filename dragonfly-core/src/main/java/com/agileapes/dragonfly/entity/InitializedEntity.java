package com.agileapes.dragonfly.entity;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 16:04)
 */
public interface InitializedEntity<E> {

    void initialize(Class<E> entityType, E entity, String key);

    String getToken();

    void setOriginalCopy(E originalCopy);

    E getOriginalCopy();

    boolean isDirtied();

}
