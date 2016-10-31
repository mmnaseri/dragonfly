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

import com.mmnaseri.dragonfly.annotations.Extension;
import com.mmnaseri.dragonfly.data.OperationType;
import com.mmnaseri.dragonfly.entity.EntityContext;
import com.mmnaseri.dragonfly.entity.EntityHandler;
import com.mmnaseri.dragonfly.fluent.tools.QueryBuilderTools;
import com.mmnaseri.dragonfly.runtime.ext.monitoring.MonitoredDataAccessObject;
import com.mmnaseri.dragonfly.runtime.ext.monitoring.MonitoredEntityContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/27 AD, 12:04)
 */
@Extension(filter = "@com.mmnaseri.dragonfly.runtime.ext.monitoring.Monitored *")
public class MonitoringExtension implements MonitoredDataAccessObject<Object, Serializable> {

    private Object entity;
    private EntityHandler<Object> entityHandler;
    private MonitoredEntityContext monitoredEntityContext;
    private EntityContext entityContext;

    @Override
    public List<Object> findAll() {
        return monitoredEntityContext.findAll(entity);
    }

    @Override
    public List<Object> findBefore(Date date) {
        return monitoredEntityContext.findBefore(entity, date);
    }

    @Override
    public List<Object> findAfter(Date date) {
        return monitoredEntityContext.findAfter(entity, date);
    }

    @Override
    public List<Object> findBetween(Date from, Date to) {
        return monitoredEntityContext.findBetween(entity, from, to);
    }

    @Override
    public List<Object> findByOperation(OperationType operationType) {
        return monitoredEntityContext.findByOperation(entity, operationType);
    }

    @Override
    public List<Object> findBefore(Serializable version) {
        return monitoredEntityContext.findBefore(entity, version);
    }

    @Override
    public List<Object> findAfter(Serializable version) {
        return monitoredEntityContext.findAfter(entity, version);
    }

    @Override
    public List<Object> findBetween(Serializable from, Serializable to) {
        return monitoredEntityContext.findBetween(entity, from, to);
    }

    @Override
    public Object find(Serializable version) {
        return monitoredEntityContext.find(entity, version);
    }

    @Override
    public void revert(Serializable version) {
        entityHandler.copy(monitoredEntityContext.revert(entity, version), entity);
    }

    @Override
    public List<Object> query(String queryName, Map<String, Object> values) {
        final List<Map<String, Object>> maps = monitoredEntityContext.executeQuery(entityHandler.getEntityType(), queryName, values);
        final ArrayList<Object> result = new ArrayList<Object>();
        for (Map<String, Object> map : maps) {
            result.add(entityHandler.fromMap(entityContext.getInstance(entityHandler.getEntityType()), map));
        }
        return result;
    }

    @Override
    public List<Object> query(String queryName, History<Object, Serializable> history) {
        return query(queryName, QueryBuilderTools.unwrap(history));
    }

    @Override
    public void setEntity(Object entity) {
        this.entity = entity;
    }

    @Override
    public void setEntityHandler(EntityHandler<Object> entityHandler) {
        this.entityHandler = entityHandler;
    }

    @Override
    public void setMonitoredEntityContext(MonitoredEntityContext monitoredEntityContext) {
        this.monitoredEntityContext = monitoredEntityContext;
    }

    @Override
    public void setEntityContext(EntityContext entityContext) {
        this.entityContext = entityContext;
    }

}
