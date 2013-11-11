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

package com.agileapes.dragonfly.events;

import com.agileapes.dragonfly.metadata.TableMetadataContext;
import com.agileapes.dragonfly.metadata.TableMetadataContextPostProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.EntityListeners;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/11, 12:11)
 */
public class CallbackResolver implements TableMetadataContextPostProcessor, EventHandlerContextPostProcessor {

    private static final Log log = LogFactory.getLog(CallbackResolver.class);
    private final Collection<DataAccessEventHandler> eventHandlers = new HashSet<DataAccessEventHandler>();
    private boolean handlersFound = false;
    private boolean contextFound = false;
    private boolean contextProcessed = false;
    private EventHandlerContext eventHandlerContext;

    public CallbackResolver() {
        log.info("Initializing event handler resolver ...");
    }

    @Override
    public synchronized void postProcessMetadataContext(TableMetadataContext tableMetadataContext) {
        log.info("Looking up entity callbacks");
        final Collection<Class<?>> entityTypes = tableMetadataContext.getEntityTypes();
        eventHandlers.add(new EntityEventCallback());
        for (Class<?> entityType : entityTypes) {
            if (entityType.isAnnotationPresent(EntityListeners.class)) {
                final EntityListeners listeners = entityType.getAnnotation(EntityListeners.class);
                for (final Class type : listeners.value()) {
                    log.info("Registering external callback: " + type);
                    eventHandlers.add(new EntityEventCallback(entityType, type));
                }
            }
        }
        handlersFound = true;
        if (contextFound) {
            registerHandlers();
        }
    }

    @Override
    public synchronized void postProcessEventHandlerContext(EventHandlerContext eventHandlerContext) {
        this.eventHandlerContext = eventHandlerContext;
        contextFound = true;
        if (handlersFound) {
            registerHandlers();
        }
    }

    private synchronized void registerHandlers() {
        if (contextProcessed) {
            return;
        }
        contextProcessed = true;
        if (!contextFound || ! handlersFound) {
            throw new IllegalStateException();
        }
        if (eventHandlers.isEmpty()) {
            log.info("No entity-specific event handlers were found.");
            return;
        }
        log.info("Registering event handlers ...");
        for (DataAccessEventHandler eventHandler : eventHandlers) {
            eventHandlerContext.addHandler(eventHandler);
        }
    }

}
