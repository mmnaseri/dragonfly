package com.agileapes.dragonfly.sample.audit;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 16:21)
 */
public interface Auditable {

    String getInsertUser();

    Date getInsertTime();

    String getUpdateUser();

    Date getUpdateTime();

    int getUpdateCount();

    void setInsertUser(String insertUser);

    void setInsertTime(Date insertTime);

    void setUpdateUser(String updateUser);

    void setUpdateTime(Date updateTime);

    void setUpdateCount(int updateCount);

}
