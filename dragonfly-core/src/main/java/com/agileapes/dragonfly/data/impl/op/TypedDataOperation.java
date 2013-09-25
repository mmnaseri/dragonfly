package com.agileapes.dragonfly.data.impl.op;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.OperationType;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 2:38)
 */
public class TypedDataOperation extends AbstractDataOperation {

    private final Class<?> entityType;

    public TypedDataOperation(DataAccess dataAccess, OperationType operationType, Class<?> entityType) {
        super(dataAccess, operationType);
        this.entityType = entityType;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

}
