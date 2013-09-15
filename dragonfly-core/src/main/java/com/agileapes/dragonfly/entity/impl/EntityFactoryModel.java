package com.agileapes.dragonfly.entity.impl;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 14:22)
 */
public class EntityFactoryModel {

    private Class<?> entityType;

    public Class<?> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<?> entityType) {
        this.entityType = entityType;
    }
}
