package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.EntityHandler;
import com.agileapes.dragonfly.entity.EntityHandlerContext;
import com.agileapes.dragonfly.entity.EntityHandlerContextPostProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 5:50)
 */
public class HandlerContextPreparatorPostProcessor implements EntityHandlerContextPostProcessor {

    private Set<EntityHandler<?>> handlers = new HashSet<EntityHandler<?>>();

    public void setHandlers(Set<EntityHandler<?>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void postProcessEntityHandlerContext(EntityHandlerContext entityHandlerContext) {
        for (EntityHandler<?> mapHandler : handlers) {
            entityHandlerContext.addHandler(mapHandler);
        }
    }
}
