/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
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

package com.mmnaseri.dragonfly.runtime.ext.audit.impl;

import com.mmnaseri.dragonfly.annotations.Extension;
import com.mmnaseri.dragonfly.runtime.ext.audit.api.ConfigurableAuditable;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/9/3 AD, 14:59)
 */
@Extension(filter = "@com.mmnaseri.dragonfly.runtime.ext.audit.api.Audited *")
public class AuditExtension implements ConfigurableAuditable {

    private String insertUser;
    private String updateUser;
    private Date insertDate;
    private Date updateDate;
    private Long updateCount;

    @Override
    @Column(length = 512)
    public String getInsertUser() {
        return insertUser;
    }

    @Override
    public void setInsertUser(String insertUser) {
        this.insertUser = insertUser;
    }

    @Override
    @Column(length = 512)
    public String getUpdateUser() {
        return updateUser;
    }

    @Override
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    public Date getInsertDate() {
        return insertDate;
    }

    @Override
    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    @Override
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    public Date getUpdateDate() {
        return updateDate;
    }

    @Override
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    @Column
    public Long getUpdateCount() {
        return updateCount;
    }

    @Override
    public void setUpdateCount(Long updateCount) {
        this.updateCount = updateCount;
    }

}
