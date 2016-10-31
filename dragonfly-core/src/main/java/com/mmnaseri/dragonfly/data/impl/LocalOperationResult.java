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

package com.mmnaseri.dragonfly.data.impl;

import com.mmnaseri.dragonfly.statement.Statements;

/**
 * This class is designed to hold results for a given operation for which results must be cached
 * for later reference.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/10/5, 9:25)
 */
public class LocalOperationResult {

    private final Class<?> entityType;
    private final Statements.Manipulation statement;
    private final Object request;
    private Object result;

    public LocalOperationResult(Class<?> entityType, Statements.Manipulation statement, Object request) {
        this.entityType = entityType;
        this.statement = statement;
        this.request = request;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalOperationResult that = (LocalOperationResult) o;
        return !(entityType != null ? !entityType.equals(that.entityType) : that.entityType != null) && !(request != null ? !request.equals(that.request) : that.request != null) && statement == that.statement;
    }

    @Override
    public int hashCode() {
        int result = entityType != null ? entityType.hashCode() : 0;
        result = 31 * result + (statement != null ? statement.hashCode() : 0);
        result = 31 * result + (request != null ? request.hashCode() : 0);
        return result;
    }
}
