package com.agileapes.dragonfly.sample.audit;

import java.util.Date;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 16:22)
 */
public class DefaultAuditable implements Auditable {

    private String updateUser;
    private Date updateTime;
    private String insertUser;
    private Date insertTime;
    private int updateCount;

    @Override
    public String getInsertUser() {
        return insertUser;
    }

    @Override
    public Date getInsertTime() {
        return insertTime;
    }

    @Override
    public String getUpdateUser() {
        return updateUser;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
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
