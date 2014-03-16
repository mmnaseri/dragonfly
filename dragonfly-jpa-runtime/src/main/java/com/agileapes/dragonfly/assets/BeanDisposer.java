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

package com.agileapes.dragonfly.assets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.Ordered;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/2, 21:19)
 */
public class BeanDisposer implements BeanNameAware, BeanFactoryPostProcessor, Ordered {

    private static final Log log = LogFactory.getLog(BeanDisposer.class);

    private String disposer;

    @Override
    public void setBeanName(String name) {
        disposer = name;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        final String[] disposables = beanFactory.getBeanNamesForType(Disposable.class, false, true);
        final BeanDefinitionRegistry definitionRegistry = (BeanDefinitionRegistry) beanFactory;
        for (String disposable : disposables) {
            dispose(definitionRegistry, disposable);
        }
        dispose(definitionRegistry, disposer);
    }

    private void dispose(BeanDefinitionRegistry definitionRegistry, String disposable) {
        log.info("Disposing of bean " + disposable);
        definitionRegistry.removeBeanDefinition(disposable);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

}
