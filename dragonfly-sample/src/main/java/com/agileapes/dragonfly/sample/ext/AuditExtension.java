package com.agileapes.dragonfly.sample.ext;

import com.agileapes.dragonfly.annotations.Extension;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 16:22)
 */
@Extension(descriptor = "!@com.agileapes.dragonfly.sample.ext.ManualIdentity *")
public class AuditExtension implements Auditable, Identifiable {

    private String updateUser;
    private Date updateTime;
    private String insertUser;
    private Date insertTime;
    private Integer updateCount;
    private Long identifier;

    @Override
    @Column(name = "insert_user", length = 256)
    public String getInsertUser() {
        return insertUser;
    }

    @Override
    @Column(name = "insert_time")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getInsertTime() {
        return insertTime;
    }

    @Override
    @Column(name = "update_user", length = 256)
    public String getUpdateUser() {
        return updateUser;
    }

    @Override
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    @Column(name = "update_count", nullable = false)
    public Integer getUpdateCount() {
        return updateCount;
    }

    @Override
    public void setInsertUser(String insertUser) {
        this.insertUser = insertUser;
    }

    @Override
    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public void setUpdateCount(Integer updateCount) {
        this.updateCount = updateCount;
    }

    @Override
    @Id
    @GeneratedValue
    @Column(name = "identifier", nullable = false)
    public Long getUniqueKey() {
        return identifier;
    }

    @Override
    public void setUniqueKey(Long identifier) {
        this.identifier = identifier;
    }

}
