/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.model;

import javax.persistence.TemporalType;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 4:07)
 */
public class PropertyAccessModel {

    private String propertyName;
    private String columnName;
    private Class<?> propertyType;
    private String getterName;
    private String setterName;
    private Class<?> declaringClass;
    private TemporalType temporalType;
    private PropertyAccessModel foreignProperty;

    public PropertyAccessModel(String propertyName, String columnName, Class<?> propertyType, String getterName, String setterName, Class<?> declaringClass, TemporalType temporalType, PropertyAccessModel foreignProperty) {
        this.propertyName = propertyName;
        this.columnName = columnName;
        this.propertyType = propertyType;
        this.getterName = getterName;
        this.setterName = setterName;
        this.declaringClass = declaringClass;
        this.temporalType = temporalType;
        this.foreignProperty = foreignProperty;
    }

    public PropertyAccessModel() {
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getGetterName() {
        return getterName;
    }

    public void setGetterName(String getterName) {
        this.getterName = getterName;
    }

    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public void setDeclaringClass(Class<?> declaringClass) {
        this.declaringClass = declaringClass;
    }

    public String getSetterName() {
        return setterName;
    }

    public void setSetterName(String setterName) {
        this.setterName = setterName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public PropertyAccessModel getForeignProperty() {
        return foreignProperty;
    }

    public void setForeignProperty(PropertyAccessModel foreignProperty) {
        this.foreignProperty = foreignProperty;
    }

    public TemporalType getTemporalType() {
        return temporalType;
    }

    public void setTemporalType(TemporalType temporalType) {
        this.temporalType = temporalType;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }
}
