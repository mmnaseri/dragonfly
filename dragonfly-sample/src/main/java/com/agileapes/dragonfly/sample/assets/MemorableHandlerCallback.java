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

package com.agileapes.dragonfly.sample.assets;

import com.agileapes.dragonfly.data.DataOperation;
import com.agileapes.dragonfly.data.OperationType;
import com.agileapes.dragonfly.data.impl.TypedDataCallback;
import com.agileapes.dragonfly.data.impl.op.IdentifiableDataOperation;
import com.agileapes.dragonfly.data.impl.op.SampledDataOperation;
import com.agileapes.dragonfly.data.impl.op.TypedDataOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/5, 10:47)
 */
@Component
public class MemorableHandlerCallback extends TypedDataCallback<DataOperation> {

    @Autowired
    private Memory memory;

    protected MemorableHandlerCallback() {
        super(Memorable.class);
    }

    @Override
    public Object execute(DataOperation operation) {
        if (OperationType.SAVE.equals(operation.getOperationType()) && operation instanceof SampledDataOperation) {
            SampledDataOperation dataOperation = (SampledDataOperation) operation;
            final Object sample = dataOperation.getSample();
            final Memorable memorable = (Memorable) sample;
            if (memorable.getId() != null) {
                memory.put(memorable.getId(), memorable);
            } else {
                memorable.setId((long) memory.size());
                memory.put(memorable.getId(), memorable);
            }
            return memorable;
        } else if (OperationType.DELETE.equals(operation.getOperationType()) && operation instanceof SampledDataOperation) {
            SampledDataOperation dataOperation = (SampledDataOperation) operation;
            Memorable memorable = (Memorable) dataOperation.getSample();
            if (memorable.getId() != null) {
                memory.remove(memorable.getId());
            } else {
                Long key = null;
                for (Map.Entry<Long, Memorable> entry : memory.entrySet()) {
                    if (entry.getValue().getName().equals(memorable.getName())) {
                        key = entry.getKey();
                    }
                }
                if (key != null) {
                    memory.remove(key);
                }
            }
            return null;
        } else if (OperationType.FIND.equals(operation.getOperationType()) && operation instanceof IdentifiableDataOperation) {
            IdentifiableDataOperation dataOperation = (IdentifiableDataOperation) operation;
            //noinspection SuspiciousMethodCalls
            final Memorable memorable = memory.get(dataOperation.getKey());
            return new Memorable(memorable.getId(), memorable.getName());
        } else if (OperationType.COUNT.equals(operation.getOperationType()) && operation instanceof TypedDataOperation) {
            return (long) memory.size();
        } else {
            return operation.proceed();
        }
    }

}
