package com.agileapes.dragonfly.sample.assets;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 15:15)
 */
public class DataAccessPreparator implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        final Collection<DataAccess> dataAccessCollection = beanFactory.getBeansOfType(DataAccess.class).values();
        final Collection<DataAccessPostProcessor> postProcessors = beanFactory.getBeansOfType(DataAccessPostProcessor.class).values();
        for (DataAccess dataAccess : dataAccessCollection) {
            for (DataAccessPostProcessor postProcessor : postProcessors) {
                postProcessor.postProcessDataAccess(dataAccess);
            }
        }
    }

}
