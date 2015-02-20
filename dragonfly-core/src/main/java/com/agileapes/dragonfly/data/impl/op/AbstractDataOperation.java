/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
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

package com.agileapes.dragonfly.data.impl.op;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.DataOperation;
import com.agileapes.dragonfly.data.OperationType;
import com.agileapes.dragonfly.error.UnsuccessfulOperationError;

/**
 * This class encapsulates an abstract data operation, providing method bodies for the most
 * common operations.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 2:22)
 */
public abstract class AbstractDataOperation implements DataOperation {

    private final OperationType operationType;
    private final DataAccess dataAccess;
    private DataCallback callback;

    public AbstractDataOperation(DataAccess dataAccess, OperationType operationType, DataCallback callback) {
        this.dataAccess = dataAccess;
        this.operationType = operationType;
        this.callback = callback;
    }

    @Override
    public DataAccess getDataAccess() {
        return dataAccess;
    }

    @Override
    public void interrupt() {
        throw new UnsuccessfulOperationError("The operation was interrupted");
    }

    @Override
    public Object proceed() {
        //noinspection unchecked
        return callback.execute(this);
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    public void setCallback(DataCallback callback) {
        this.callback = callback;
    }

    protected abstract String getAsString();

    @Override
    public String toString() {
        return operationType + " " + getAsString();
    }

    public DataCallback getCallback() {
        return callback;
    }
}
