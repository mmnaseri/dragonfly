package com.agileapes.dragonfly.data.impl.op;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.DataOperation;
import com.agileapes.dragonfly.data.OperationType;

/**
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
