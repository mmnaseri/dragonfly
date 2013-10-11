package com.agileapes.dragonfly.events;

import com.agileapes.dragonfly.metadata.MetadataContext;
import com.agileapes.dragonfly.metadata.MetadataContextPostProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.EntityListeners;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/11, 12:11)
 */
public class CallbackResolver implements MetadataContextPostProcessor, EventHandlerContextPostProcessor {

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
    public synchronized void postProcessMetadataContext(MetadataContext metadataContext) {
        log.info("Looking up entity callbacks");
        final Collection<Class<?>> entityTypes = metadataContext.getEntityTypes();
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
