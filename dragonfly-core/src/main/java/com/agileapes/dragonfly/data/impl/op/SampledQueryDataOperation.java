package com.agileapes.dragonfly.data.impl.op;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.OperationType;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 3:16)
 */
public class SampledQueryDataOperation extends SampledDataOperation {

    private final String queryName;

    public SampledQueryDataOperation(DataAccess dataAccess, OperationType operationType, Object sample, String queryName) {
        super(dataAccess, operationType, sample);
        this.queryName = queryName;
    }

    public String getQueryName() {
        return queryName;
    }
}
