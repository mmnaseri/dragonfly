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

package com.agileapes.dragonfly.sample.entities;

import com.agileapes.dragonfly.runtime.ext.identity.api.Identified;
import com.agileapes.dragonfly.runtime.ext.monitoring.Monitored;
import com.agileapes.dragonfly.runtime.ext.monitoring.MonitoringQuery;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/27 AD, 12:01)
 */
@Entity
@Table(name = "monitored_entities")
@Monitored
@MonitoringQuery(
        name = MonitoredEntity.CLEAR_HISTORY,
        query = "DELETE FROM ${qualify(table)} WHERE ${qualify(metadata)} = ${value.key}"
)
@Identified
public class MonitoredEntity {

    public static final String CLEAR_HISTORY = "clearHistory";

    private String name;
    private Date date;
    private Long version;
    private RelatedEntity relatedEntity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Version
    @Column(nullable = false)
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @JoinColumn
    @OneToOne(cascade = CascadeType.REFRESH)
    public RelatedEntity getRelatedEntity() {
        return relatedEntity;
    }

    public void setRelatedEntity(RelatedEntity relatedEntity) {
        this.relatedEntity = relatedEntity;
    }

}
