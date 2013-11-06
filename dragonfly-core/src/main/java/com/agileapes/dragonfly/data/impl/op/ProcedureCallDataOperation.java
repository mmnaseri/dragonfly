/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.data.impl.op;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.OperationType;

/**
 * This operation represents a procedure call. Parameters passed to the procedure call are
 * available.
 *
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

    @Override
    public String getAsString() {
        return getEntityType().getSimpleName() + "." + procedureName + "()";
    }
}
