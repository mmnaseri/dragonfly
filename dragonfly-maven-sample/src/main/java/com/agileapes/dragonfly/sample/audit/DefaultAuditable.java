package com.agileapes.dragonfly.sample.audit;

import com.agileapes.dragonfly.annotations.Extension;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 16:22)
 */
@Extension
public class DefaultAuditable implements Auditable {

    private String updateUser;
    private Date updateTime;
    private String insertUser;
    private Date insertTime;
    private int updateCount;

    @Override
    @Column
    public String getInsertUser() {
        return insertUser;
    }

    @Override
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date getInsertTime() {
        return insertTime;
    }

    @Override
    @Column
    public String getUpdateUser() {
        return updateUser;
    }

    @Override
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    @Column
    public int getUpdateCount() {
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
    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

}
