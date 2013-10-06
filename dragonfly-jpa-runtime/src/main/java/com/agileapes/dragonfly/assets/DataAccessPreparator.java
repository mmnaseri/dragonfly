package com.agileapes.dragonfly.assets;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessPostProcessor;
import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.impl.DelegatingDataAccess;
import com.agileapes.dragonfly.entity.EntityHandlerContext;
import com.agileapes.dragonfly.entity.EntityHandlerContextPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 15:15)
 */
public class DataAccessPreparator implements ApplicationContextAware {

    private void handleDelegationCallbacks(ListableBeanFactory beanFactory) {
        final Map<String,DataCallback> callbacks = beanFactory.getBeansOfType(DataCallback.class, false, true);
        final Map<String, DelegatingDataAccess> dataAccessCollection = beanFactory.getBeansOfType(DelegatingDataAccess.class, false, true);
        for (DelegatingDataAccess dataAccess : dataAccessCollection.values()) {
            for (DataCallback callback : callbacks.values()) {
                dataAccess.addCallback(callback);
            }
        }
    }

    private void postProcessEntityHandlerContext(ListableBeanFactory beanFactory) {
        final Collection<EntityHandlerContextPostProcessor> postProcessors = beanFactory.getBeansOfType(EntityHandlerContextPostProcessor.class, false, true).values();
        final Collection<EntityHandlerContext> contexts = beanFactory.getBeansOfType(EntityHandlerContext.class, false, true).values();
        for (EntityHandlerContext context : contexts) {
            for (EntityHandlerContextPostProcessor postProcessor : postProcessors) {
                postProcessor.postProcessEntityHandlerContext(context);
            }
        }
    }

    private void postProcessDataAccess(ListableBeanFactory beanFactory) {
        final Collection<DataAccess> dataAccessCollection = beanFactory.getBeansOfType(DataAccess.class, false, true).values();
        final Collection<DataAccessPostProcessor> postProcessors = beanFactory.getBeansOfType(DataAccessPostProcessor.class, false, true).values();
        for (DataAccess dataAccess : dataAccessCollection) {
            for (DataAccessPostProcessor postProcessor : postProcessors) {
                postProcessor.postProcessDataAccess(dataAccess);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext beanFactory) throws BeansException {
        postProcessDataAccess(beanFactory);
        postProcessEntityHandlerContext(beanFactory);
        handleDelegationCallbacks(beanFactory);
    }

    public DataAccessPreparator() {
        System.out.println();
    }
}
