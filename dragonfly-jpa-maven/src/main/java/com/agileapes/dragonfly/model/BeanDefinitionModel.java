package com.agileapes.dragonfly.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 1:46)
 */
public class BeanDefinitionModel {

    private String id;
    private String type;
    private final Map<String, String> properties = new HashMap<String, String>();
    private final Map<String, String> references = new HashMap<String, String>();

    public BeanDefinitionModel() {
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

    public Map<String, String> getProperties() {
        return properties;
    }

    public Map<String, String> getReferences() {
        return references;
    }

    public void setProperty(String property, String value) {
        properties.put(property, value);
    }

    public void setReference(String property, String reference) {
        references.put(property, reference);
    }
}
