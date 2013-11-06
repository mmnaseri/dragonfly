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

package com.agileapes.dragonfly.sample.entities;

import com.agileapes.dragonfly.annotations.Order;
import com.agileapes.dragonfly.annotations.Ordering;
import com.agileapes.dragonfly.sample.assets.StationUpdateMonitor;
import com.agileapes.dragonfly.sample.ext.ManualIdentity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/11, 13:02)
 */
@Entity
@Table(
        name = "stations",
        schema = "test"
)
@ManualIdentity
@EntityListeners(StationUpdateMonitor.class)
public class Station {

    private String name;
    private Long id;
    private Date creationDate;
    private Date updateDate;
    private Integer version;

    @Column
    @Order(priority = 2, value = Ordering.DESCENDING)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    @Id
    @GeneratedValue
    @Order(priority = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @PostPersist
    private void setCreationDate() {
        setCreationDate(new Date());
    }

    @Version
    @Column(nullable = false)
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
