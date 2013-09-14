package com.agileapes.dragonfly.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 4:05)
 */
public class BeanMapperModel {

    private final Set<PropertyAccessModel> properties = new HashSet<PropertyAccessModel>();
    private Class<?> entityType;

    public Set<PropertyAccessModel> getProperties() {
        return properties;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<?> entityType) {
        this.entityType = entityType;
    }

    public void addProperty(PropertyAccessModel property) {
        properties.add(property);
    }

}
