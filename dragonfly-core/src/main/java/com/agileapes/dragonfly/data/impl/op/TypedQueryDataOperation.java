package com.agileapes.dragonfly.data.impl.op;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.OperationType;

import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 3:17)
 */
public class TypedQueryDataOperation extends TypedDataOperation {

    private final String queryName;
    private final Map<String, Object> map;

    public TypedQueryDataOperation(DataAccess dataAccess, OperationType operationType, Class<?> entityType, String queryName, Map<String, Object> map, DataCallback callback) {
        super(dataAccess, operationType, entityType, callback);
        this.queryName = queryName;
        this.map = map;
    }

    public String getQueryName() {
        return queryName;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    @Override
    protected String getAsString() {
        return getEntityType().getSimpleName() + "." + getQueryName() + "{}";
    }
}
