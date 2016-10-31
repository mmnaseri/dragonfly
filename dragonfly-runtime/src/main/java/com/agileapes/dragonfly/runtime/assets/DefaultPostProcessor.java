/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.agileapes.dragonfly.runtime.assets;

import com.mmnaseri.couteau.context.impl.OrderedBeanComparator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

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
        this.postProcessor.postProcess(context, postProcessor);
    }

}
