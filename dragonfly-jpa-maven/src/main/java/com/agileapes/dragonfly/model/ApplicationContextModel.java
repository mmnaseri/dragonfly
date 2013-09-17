package com.agileapes.dragonfly.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 1:24)
 */
public class ApplicationContextModel {

    private final Set<BeanDefinitionModel> beans = new HashSet<BeanDefinitionModel>();
    private final BeanIdentifierMethod identify = new BeanIdentifierMethod();
    private final EscapeStringMethod escape = new EscapeStringMethod();

    public Set<BeanDefinitionModel> getBeans() {
        return beans;
    }

    public void addBean(BeanDefinitionModel beanDefinitionModel) {
        this.beans.add(beanDefinitionModel);
    }

    public BeanIdentifierMethod getIdentify() {
        return identify;
    }

    public EscapeStringMethod getEscape() {
        return escape;
    }
}
