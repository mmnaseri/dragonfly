package com.agileapes.dragonfly.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 1:24)
 */
public class ApplicationContextModel {

    private final Set<BeanDefinitionModel> beans = new HashSet<BeanDefinitionModel>();

    public Set<BeanDefinitionModel> getBeans() {
        return beans;
    }

    public void addBean(BeanDefinitionModel beanDefinitionModel) {
        this.beans.add(beanDefinitionModel);
    }
}
