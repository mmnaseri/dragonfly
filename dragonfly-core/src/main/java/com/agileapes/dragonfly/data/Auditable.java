package com.agileapes.dragonfly.data;

import java.sql.Timestamp;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 21:09)
 */
public interface Auditable {

    String getUpdateUser();

    void setUpdateUser(String updateUser);

    Timestamp getUpdateTime();

    void setUpdateTime(Timestamp updateTime);

}
