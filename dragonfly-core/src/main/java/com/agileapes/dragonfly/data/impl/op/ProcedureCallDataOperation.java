package com.agileapes.dragonfly.data.impl.op;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.OperationType;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 3:22)
 */
public class ProcedureCallDataOperation extends TypedDataOperation {

    private final String procedureName;
    private final Object[] parameters;

    public ProcedureCallDataOperation(DataAccess dataAccess, OperationType operationType, Class<?> entityType, String procedureName, Object[] parameters, DataCallback callback) {
        super(dataAccess, operationType, entityType, callback);
        this.procedureName = procedureName;
        this.parameters = parameters;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
