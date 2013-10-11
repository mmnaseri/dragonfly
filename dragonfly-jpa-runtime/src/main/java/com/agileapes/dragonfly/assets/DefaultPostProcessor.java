package com.agileapes.dragonfly.assets;

import com.agileapes.couteau.context.impl.OrderedBeanComparator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/11, 12:21)
 */
class DefaultPostProcessor<C, P> implements PostProcessor<C, P>, Runnable, ApplicationContextAware {

    private final Class<C> contextType;
    private final Class<P> postProcessorType;
    private final PostProcessor<C, P> postProcessor;
    private ApplicationContext applicationContext;

    DefaultPostProcessor(Class<C> contextType, Class<P> postProcessorType, PostProcessor<C, P> postProcessor) {
        this.contextType = contextType;
        this.postProcessorType = postProcessorType;
        this.postProcessor = postProcessor;
    }

    Class<C> getContextType() {
        return contextType;
    }

    Class<P> getPostProcessorType() {
        return postProcessorType;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        if (applicationContext == null) {
            throw new IllegalStateException();
        }
        final List<C> contexts = with(applicationContext.getBeansOfType(contextType, false, true).values()).sort(new OrderedBeanComparator()).list();
        final List<P> postProcessors = with(applicationContext.getBeansOfType(postProcessorType, false, true).values()).sort(new OrderedBeanComparator()).list();
        for (C context : contexts) {
            for (P postProcessor : postProcessors) {
                postProcess(context, postProcessor);
            }
        }
    }

    @Override
    public void postProcess(C context, P postProcessor) {
        this.postProcessor.postProcess(context, postProcessor);;
    }

}
