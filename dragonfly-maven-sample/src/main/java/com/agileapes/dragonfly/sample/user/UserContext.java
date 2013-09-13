package com.agileapes.dragonfly.sample.user;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 13:00)
 */
public class UserContext implements BeanPostProcessor, BeanFactoryPostProcessor {

    public String getCurrentUser() {
        return "SOMEBODY";
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof UserContextAware) {
            ((UserContextAware) bean).setUserContext(this);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof UserContextAware) {
            ((UserContextAware) bean).setUserContext(this);
        }
        return bean;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        final Collection<UserContextAware> contextAwares = beanFactory.getBeansOfType(UserContextAware.class, false, true).values();
        for (UserContextAware contextAware : contextAwares) {
            contextAware.setUserContext(this);
        }
    }

}
