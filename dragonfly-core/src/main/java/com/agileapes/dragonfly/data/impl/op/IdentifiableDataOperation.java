package com.agileapes.dragonfly.data.impl.op;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.OperationType;

import java.io.Serializable;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 2:34)
 */
public class IdentifiableDataOperation extends AbstractDataOperation {

    private final Class<?> entityType;
    private final Serializable key;

    public IdentifiableDataOperation(DataAccess dataAccess, OperationType operationType, Class<?> entityType, Serializable key) {
        super(dataAccess, operationType);
        this.entityType = entityType;
        this.key = key;
    }


    public Class<?> getEntityType() {
        return entityType;
    }

    public Serializable getKey() {
        return key;
    }

}
