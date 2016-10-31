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

package com.mmnaseri.dragonfly.sample.entities;

import com.mmnaseri.dragonfly.annotations.BasicCollection;
import com.mmnaseri.dragonfly.runtime.ext.identity.api.Identified;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/5/31 AD, 11:57)
 */
@Entity
@Table(
        schema = "test",
        name = "switch"
)
@NamedNativeQuery(
        name = "selectDate",
        query = "SELECT ${qualify(column('date'))} FROM ${qualify(table)};"
)
@Identified
public class Switch extends AbstractSwitch {

    private SwitchType type;
    private Boolean on;
    private Date date;
    private Collection<String> names;

    public SwitchType getType() {
        return type;
    }

    public void setType(SwitchType type) {
        this.type = type;
    }

    public Boolean isOn() {
        return on;
    }

    public void setOn(Boolean on) {
        this.on = on;
    }

    @Temporal(TemporalType.DATE)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    @Transient
    public String getSwitchCode() {
        return super.getSwitchCode();
    }

    @BasicCollection
    public Collection<String> getNames() {
        return names;
    }

    public void setNames(Collection<String> names) {
        this.names = names;
    }
}
