package com.agileapes.dragonfly.assets;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/11, 12:26)
 */
public class AsynchronousPostProcessor<C, P> extends Thread implements ApplicationContextAware {

    private final DefaultPostProcessor<C, P> postProcessor;

    public AsynchronousPostProcessor(Class<C> contextType, Class<P> postProcessorType, PostProcessor<C, P> callback) {
        super("postProcessor:" + postProcessorType.getSimpleName() + "@" + contextType.getSimpleName());
        this.postProcessor = new DefaultPostProcessor<C, P>(contextType, postProcessorType, callback);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        postProcessor.setApplicationContext(applicationContext);
    }

    @Override
    public synchronized void start() {
        postProcessor.run();
    }
}
