package com.agileapes.dragonfly.data.impl.op;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataOperation;
import com.agileapes.dragonfly.data.OperationType;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 2:22)
 */
public abstract class AbstractDataOperation implements DataOperation {

    private final OperationType operationType;
    private final DataAccess dataAccess;

    public AbstractDataOperation(DataAccess dataAccess, OperationType operationType) {
        this.dataAccess = dataAccess;
        this.operationType = operationType;
    }

    @Override
    public DataAccess getDataAccess() {
        return dataAccess;
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

}
