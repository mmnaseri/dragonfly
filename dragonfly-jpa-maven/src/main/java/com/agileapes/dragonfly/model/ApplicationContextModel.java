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
