package com.agileapes.dragonfly;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/2, 21:19)
 */
public class BeanDisposer implements BeanNameAware, BeanFactoryPostProcessor {

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

}
