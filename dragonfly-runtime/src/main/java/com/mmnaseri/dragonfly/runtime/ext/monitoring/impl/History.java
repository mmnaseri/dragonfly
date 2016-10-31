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

package com.mmnaseri.dragonfly.runtime.ext.monitoring.impl;

import com.mmnaseri.dragonfly.data.OperationType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/28 AD, 15:48)
 */
public class History<E, K extends Serializable> {

    private final K key;
    private Date date;
    private Date fromDate;
    private Date toDate;
    private Serializable version;
    private Serializable fromVersion;
    private Serializable toVersion;
    private OperationType operation;

    public History(K key) {
        this.key = key;
    }

    public Date getDate() {
        return date;
    }

    public Serializable getVersion() {
        return version;
    }

    public K getKey() {
        return key;
    }

    public OperationType getOperation() {
        return operation;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public Serializable getFromVersion() {
        return fromVersion;
    }

    public Serializable getToVersion() {
        return toVersion;
    }

    public History<E, K> setDate(Date date) {
        this.date = date;
        return this;
    }

    public History<E, K> setVersion(Serializable version) {
        this.version = version;
        return this;
    }

    public History<E, K> setOperation(OperationType operation) {
        this.operation = operation;
        return this;
    }

    public History<E, K> setFromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public History<E, K> setToDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    public History<E, K> setFromVersion(Serializable fromVersion) {
        this.fromVersion = fromVersion;
        return this;
    }

    public History<E, K> setToVersion(Serializable toVersion) {
        this.toVersion = toVersion;
        return this;
    }

}
