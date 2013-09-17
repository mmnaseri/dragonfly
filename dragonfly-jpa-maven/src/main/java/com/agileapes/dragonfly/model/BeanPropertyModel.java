package com.agileapes.dragonfly.model;

import java.util.List;
import java.util.Set;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 16:36)
 */
public class BeanPropertyModel {

    private final String name;
    private final String type;
    private final Object value;
    private final BeanDefinitionModel reference;
    private final boolean set;
    private final boolean list;

    public BeanPropertyModel(BeanDefinitionModel reference) {
        this.reference = reference;
        this.type = reference.getType();
        this.name = null;
        this.value = null;
        this.set = false;
        this.list = false;
    }

    public BeanPropertyModel(String qualifier, Object value) {
        this.reference = null;
        this.type = qualifier;
        this.name = qualifier;
        this.value = value;
        this.set = value instanceof Set;
        this.list = value instanceof List;
    }

    public BeanPropertyModel(String name, BeanDefinitionModel reference) {
        this(name, reference.getType(), reference);
    }

    public BeanPropertyModel(String name, String type, BeanDefinitionModel reference) {
        this.name = name;
        this.reference = reference;
        this.type = type;
        this.value = null;
        this.set = false;
        this.list = false;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public BeanDefinitionModel getReference() {
        return reference;
    }

    public String getName() {
        return name;
    }

    public boolean isSet() {
        return set;
    }

    public boolean isList() {
        return list;
    }
}
