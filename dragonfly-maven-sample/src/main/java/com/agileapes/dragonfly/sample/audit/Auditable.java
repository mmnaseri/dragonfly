package com.agileapes.dragonfly.sample.audit;

import java.util.Date;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 16:21)
 */
public interface Auditable {

    String getUpdateUser();

    Date getUpdateTime();

    void setUpdateUser(String updateUser);

    void setUpdateTime(Date updateTime);

}
