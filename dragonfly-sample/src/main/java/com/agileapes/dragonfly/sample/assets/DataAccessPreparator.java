package com.agileapes.dragonfly.sample.assets;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessPostProcessor;
import com.agileapes.dragonfly.entity.EntityHandlerContext;
import com.agileapes.dragonfly.entity.EntityHandlerContextPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 15:15)
 */
@Component
public class DataAccessPreparator implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        postProcessDataAccess(beanFactory);
        postProcessEntityHandlerContext(beanFactory);
    }

    private void postProcessEntityHandlerContext(ConfigurableListableBeanFactory beanFactory) {
        final Collection<EntityHandlerContextPostProcessor> postProcessors = beanFactory.getBeansOfType(EntityHandlerContextPostProcessor.class, false, true).values();
        final Collection<EntityHandlerContext> contexts = beanFactory.getBeansOfType(EntityHandlerContext.class, false, true).values();
        for (EntityHandlerContext context : contexts) {
            for (EntityHandlerContextPostProcessor postProcessor : postProcessors) {
                postProcessor.postProcessEntityHandlerContext(context);
            }
        }
    }

    private void postProcessDataAccess(ConfigurableListableBeanFactory beanFactory) {
        final Collection<DataAccess> dataAccessCollection = beanFactory.getBeansOfType(DataAccess.class, false, true).values();
        final Collection<DataAccessPostProcessor> postProcessors = beanFactory.getBeansOfType(DataAccessPostProcessor.class, false, true).values();
        for (DataAccess dataAccess : dataAccessCollection) {
            for (DataAccessPostProcessor postProcessor : postProcessors) {
                postProcessor.postProcessDataAccess(dataAccess);
            }
        }
    }

}
