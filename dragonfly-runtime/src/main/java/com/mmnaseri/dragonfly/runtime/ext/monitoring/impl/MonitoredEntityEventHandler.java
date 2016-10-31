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

package com.mmnaseri.dragonfly.runtime.ext.monitoring.impl;

import com.mmnaseri.dragonfly.data.OperationType;
import com.mmnaseri.dragonfly.events.impl.AbstractDataAccessEventHandler;
import com.mmnaseri.dragonfly.runtime.ext.monitoring.MonitoredEntityContext;
import com.mmnaseri.dragonfly.runtime.ext.monitoring.MonitoredEntityContextAware;

import java.io.Serializable;
import java.util.List;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/28 AD, 15:25)
 */
public class MonitoredEntityEventHandler extends AbstractDataAccessEventHandler {

    private final MonitoredEntityContext context;

    public MonitoredEntityEventHandler(MonitoredEntityContext context) {
        this.context = context;
    }

    @Override
    public <E> void afterFind(E sample, List<E> entities) {
        for (E entity : entities) {
            if (entity instanceof MonitoredEntityContextAware) {
                ((MonitoredEntityContextAware) entity).setMonitoredEntityContext(context);
            }
        }
    }

    @Override
    public <E, K extends Serializable> E afterFind(Class<E> entityType, K key, E entity) {
        if (entity instanceof MonitoredEntityContextAware) {
            ((MonitoredEntityContextAware) entity).setMonitoredEntityContext(context);
        }
        return entity;
    }

    @Override
    public <E> void afterFindAll(Class<E> entityType, List<E> entities) {
        for (E entity : entities) {
            if (entity instanceof MonitoredEntityContextAware) {
                ((MonitoredEntityContextAware) entity).setMonitoredEntityContext(context);
            }
        }
    }

    @Override
    public <E> void afterSave(E entity) {
        if (entity instanceof MonitoredEntityContextAware) {
            ((MonitoredEntityContextAware) entity).setMonitoredEntityContext(context);
        }
    }

    @Override
    public <E> void afterInsert(E entity) {
        if (entity instanceof MonitoredEntityContextAware) {
            ((MonitoredEntityContextAware) entity).setMonitoredEntityContext(context);
        }
        if (context.hasHistory(entity)) {
            context.note(OperationType.INSERT, entity);
        }
    }

    @Override
    public <E> void afterUpdate(E entity, boolean updated) {
        if (entity instanceof MonitoredEntityContextAware) {
            ((MonitoredEntityContextAware) entity).setMonitoredEntityContext(context);
        }
        if (context.hasHistory(entity)) {
            context.note(OperationType.UPDATE, entity);
        }
    }

    @Override
    public <E> void afterDelete(E entity) {
        if (context.hasHistory(entity)) {
            context.note(OperationType.DELETE, entity);
        }
    }

}
