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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
