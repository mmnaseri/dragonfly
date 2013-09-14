package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessPostProcessor;
import com.agileapes.dragonfly.entity.EntityHandler;
import com.agileapes.dragonfly.entity.EntityHandlerContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 5:50)
 */
public class HandlerContextPreparatorPostProcessor implements DataAccessPostProcessor {

    private Set<EntityHandler<?>> mapHandlers = new HashSet<EntityHandler<?>>();

    public void setMapHandlers(Set<EntityHandler<?>> mapHandlers) {
        this.mapHandlers = mapHandlers;
    }

    @Override
    public void postProcessDataAccess(DataAccess dataAccess) {
        final EntityHandlerContext context = dataAccess.getHandlerContext();
        for (EntityHandler<?> mapHandler : mapHandlers) {
            context.addMapHandler(mapHandler);
        }
    }

}
