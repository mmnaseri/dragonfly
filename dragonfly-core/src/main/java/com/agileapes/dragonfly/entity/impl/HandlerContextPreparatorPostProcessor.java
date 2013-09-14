package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessPostProcessor;
import com.agileapes.dragonfly.entity.EntityMapHandler;
import com.agileapes.dragonfly.entity.EntityMapHandlerContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 5:50)
 */
public class HandlerContextPreparatorPostProcessor implements DataAccessPostProcessor {

    private Set<EntityMapHandler<?>> mapHandlers = new HashSet<EntityMapHandler<?>>();

    public void setMapHandlers(Set<EntityMapHandler<?>> mapHandlers) {
        this.mapHandlers = mapHandlers;
    }

    @Override
    public void postProcessDataAccess(DataAccess dataAccess) {
        final EntityMapHandlerContext context = dataAccess.getHandlerContext();
        for (EntityMapHandler<?> mapHandler : mapHandlers) {
            context.addMapHandler(mapHandler);
        }
    }

}
