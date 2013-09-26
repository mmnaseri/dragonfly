package com.agileapes.dragonfly.data.impl.op;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.OperationType;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 2:36)
 */
public class SampledDataOperation extends AbstractDataOperation {

    private final Object sample;

    public SampledDataOperation(DataAccess dataAccess, OperationType operationType, Object sample, DataCallback callback) {
        super(dataAccess, operationType, callback);
        this.sample = sample;
    }

    public Object getSample() {
        return sample;
    }

}
