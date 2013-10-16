package com.agileapes.dragonfly.sample.entities;

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
    private int version;

    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    @Id
    @GeneratedValue
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

    @Column
    @Version
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
