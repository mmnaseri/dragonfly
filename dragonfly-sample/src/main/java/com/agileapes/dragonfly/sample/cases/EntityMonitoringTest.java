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

package com.agileapes.dragonfly.sample.cases;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.runtime.ext.identity.api.Identifiable;
import com.agileapes.dragonfly.runtime.ext.monitoring.MonitoredDataAccessObject;
import com.agileapes.dragonfly.runtime.ext.monitoring.MonitoredEntityContext;
import com.agileapes.dragonfly.runtime.ext.monitoring.impl.History;
import com.agileapes.dragonfly.sample.entities.MonitoredEntity;
import com.agileapes.dragonfly.sample.entities.RelatedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/27 AD, 12:06)
 */
@Service
public class EntityMonitoringTest extends BaseTestCase {

    @Autowired
    private DataAccess dataAccess;

    @Autowired
    private MonitoredEntityContext monitoredEntityContext;

    @Override
    public void run() {
        MonitoredEntity entity = new MonitoredEntity();
        final RelatedEntity firstRelatedEntity = new RelatedEntity();
        firstRelatedEntity.setName("First");
        entity.setRelatedEntity(dataAccess.save(firstRelatedEntity));
        final String firstName = "My Entity";
        entity.setName(firstName);
        entity = dataAccess.save(entity);
        if (entity.getVersion() != 1L) {
            throw new Error("Invalid version");
        }
        final RelatedEntity secondRelatedEntity = new RelatedEntity();
        secondRelatedEntity.setName("Second");
        entity.setRelatedEntity(dataAccess.save(secondRelatedEntity));
        final Long firstVersion = entity.getVersion();
        final String secondName = "My Other Entity";
        entity.setName(secondName);
        entity = dataAccess.save(entity);
        if (entity.getVersion() != 2L) {
            throw new Error("Invalid version");
        }
        //noinspection unchecked
        final MonitoredDataAccessObject<MonitoredEntity, Long> monitored = (MonitoredDataAccessObject<MonitoredEntity, Long>) entity;
        final List<MonitoredEntity> entities = monitored.findAll();
        expect(entities.size()).toBe(2);
        expect(entity.getName()).toEqual(secondName);
        monitored.revert(firstVersion);
        entity = dataAccess.find(MonitoredEntity.class, ((Identifiable) entity).getUniqueKey());
        expect(entity.getVersion()).toEqual(3L);
        expect(entity.getName()).toEqual(firstName);
        expect(entity.getRelatedEntity()).not().toBeNull();
        expect(entity.getRelatedEntity().getName()).toEqual(firstRelatedEntity.getName());
        expect(monitored.findBefore(4L).size()).toBe(3);
        monitoredEntityContext.executeUpdate(MonitoredEntity.class, MonitoredEntity.CLEAR_HISTORY, new History<MonitoredEntity, Long>(((Identifiable) entity).getUniqueKey()));
        expect(monitored.findAll()).toBeEmpty();
        dataAccess.deleteAll(RelatedEntity.class);
        dataAccess.deleteAll(MonitoredEntity.class);
    }

}
