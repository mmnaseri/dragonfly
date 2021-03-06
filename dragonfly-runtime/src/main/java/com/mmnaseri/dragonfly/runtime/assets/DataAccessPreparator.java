/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
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

package com.mmnaseri.dragonfly.runtime.assets;

import com.mmnaseri.dragonfly.data.DataAccess;
import com.mmnaseri.dragonfly.data.DataAccessPostProcessor;
import com.mmnaseri.dragonfly.data.DataCallback;
import com.mmnaseri.dragonfly.data.impl.DelegatingDataAccess;
import com.mmnaseri.dragonfly.entity.EntityHandlerContext;
import com.mmnaseri.dragonfly.entity.EntityHandlerContextPostProcessor;
import com.mmnaseri.dragonfly.events.EventHandlerContext;
import com.mmnaseri.dragonfly.events.EventHandlerContextPostProcessor;
import com.mmnaseri.dragonfly.metadata.TableMetadataContext;
import com.mmnaseri.dragonfly.metadata.TableMetadataContextPostProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/13, 15:15)
 */
public class DataAccessPreparator implements ApplicationContextAware {

    private static final Log log = LogFactory.getLog(DataAccessPreparator.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        final Collection<AsynchronousPostProcessor<?, ?>> postProcessors = new HashSet<AsynchronousPostProcessor<?, ?>>();
        postProcessors.add(new AsynchronousPostProcessor<EntityHandlerContext, EntityHandlerContextPostProcessor>(EntityHandlerContext.class, EntityHandlerContextPostProcessor.class, new PostProcessor<EntityHandlerContext, EntityHandlerContextPostProcessor>() {
            @Override
            public void postProcess(EntityHandlerContext context, EntityHandlerContextPostProcessor postProcessor) {
                postProcessor.postProcessEntityHandlerContext(context);
            }
        }));
        postProcessors.add(new AsynchronousPostProcessor<DelegatingDataAccess, DataCallback>(DelegatingDataAccess.class, DataCallback.class, new PostProcessor<DelegatingDataAccess, DataCallback>() {
            @Override
            public void postProcess(DelegatingDataAccess context, DataCallback postProcessor) {
                context.addCallback(postProcessor);
            }
        }));
        postProcessors.add(new AsynchronousPostProcessor<EventHandlerContext, EventHandlerContextPostProcessor>(EventHandlerContext.class, EventHandlerContextPostProcessor.class, new PostProcessor<EventHandlerContext, EventHandlerContextPostProcessor>() {
            @Override
            public void postProcess(EventHandlerContext context, EventHandlerContextPostProcessor postProcessor) {
                postProcessor.postProcessEventHandlerContext(context);
            }
        }));
        postProcessors.add(new AsynchronousPostProcessor<TableMetadataContext, TableMetadataContextPostProcessor>(TableMetadataContext.class, TableMetadataContextPostProcessor.class, new PostProcessor<TableMetadataContext, TableMetadataContextPostProcessor>() {
            @Override
            public void postProcess(TableMetadataContext context, TableMetadataContextPostProcessor postProcessor) {
                postProcessor.postProcessMetadataContext(context);
            }
        }));
        final DefaultPostProcessor<DataAccess, DataAccessPostProcessor> dataAccessInitializer = new DefaultPostProcessor<DataAccess, DataAccessPostProcessor>(DataAccess.class, DataAccessPostProcessor.class, new PostProcessor<DataAccess, DataAccessPostProcessor>() {
            @Override
            public void postProcess(DataAccess context, DataAccessPostProcessor postProcessor) {
                postProcessor.postProcessDataAccess(context);
            }
        });
        dataAccessInitializer.setApplicationContext(applicationContext);
        for (AsynchronousPostProcessor<?, ?> postProcessor : postProcessors) {
            log.info("Starting post processor: " + postProcessor.getName());
            postProcessor.setApplicationContext(applicationContext);
            postProcessor.start();
        }
        log.info("Waiting for post processors to finish ...");
        for (AsynchronousPostProcessor<?, ?> postProcessor : postProcessors) {
            try {
                postProcessor.join();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        log.info("Initializing data access ...");
        dataAccessInitializer.run();
    }

}
