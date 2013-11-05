package com.agileapes.dragonfly.data.impl.op;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.OperationType;

import java.io.Serializable;

/**
 * This class represents an operation with a key and an entity type.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 2:34)
 */
public class IdentifiableDataOperation extends TypedDataOperation {

    private final Serializable key;

    public IdentifiableDataOperation(DataAccess dataAccess, OperationType operationType, Class<?> entityType, Serializable key, DataCallback callback) {
        super(dataAccess, operationType, entityType, callback);
        this.key = key;
    }


    public Serializable getKey() {
        return key;
    }

    @Override
    public String getAsString() {
        return "the " + getEntityType().getSimpleName() + " with key " + key;
    }

}
