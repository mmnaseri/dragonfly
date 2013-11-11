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

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessPostProcessor;
import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.impl.DelegatingDataAccess;
import com.agileapes.dragonfly.entity.EntityHandlerContext;
import com.agileapes.dragonfly.entity.EntityHandlerContextPostProcessor;
import com.agileapes.dragonfly.events.EventHandlerContext;
import com.agileapes.dragonfly.events.EventHandlerContextPostProcessor;
import com.agileapes.dragonfly.metadata.TableMetadataContext;
import com.agileapes.dragonfly.metadata.TableMetadataContextPostProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
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
