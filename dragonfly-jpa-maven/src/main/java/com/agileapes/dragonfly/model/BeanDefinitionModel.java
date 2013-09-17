package com.agileapes.dragonfly.model;

import java.util.*;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 1:46)
 */
public class BeanDefinitionModel {

    private String id;
    private String type;
    private final Set<BeanPropertyModel> properties = new HashSet<BeanPropertyModel>();
    private final List<BeanPropertyModel> arguments = new ArrayList<BeanPropertyModel>();

    public BeanDefinitionModel(String type) {
        this(null, type);
    }

    public BeanDefinitionModel(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<BeanPropertyModel> getProperties() {
        return properties;
    }

    public List<BeanPropertyModel> getArguments() {
        return arguments;
    }

    public void setProperty(BeanPropertyModel property) {
        properties.add(property);
    }

    public void addConstructorArgument(BeanPropertyModel argument) {
        arguments.add(argument);
    }

}
