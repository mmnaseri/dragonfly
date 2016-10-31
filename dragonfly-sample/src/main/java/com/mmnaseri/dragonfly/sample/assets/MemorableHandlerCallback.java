/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mmnaseri.dragonfly.sample.assets;

import com.mmnaseri.dragonfly.data.DataOperation;
import com.mmnaseri.dragonfly.data.OperationType;
import com.mmnaseri.dragonfly.data.impl.TypedDataCallback;
import com.mmnaseri.dragonfly.data.impl.op.IdentifiableDataOperation;
import com.mmnaseri.dragonfly.data.impl.op.SampledDataOperation;
import com.mmnaseri.dragonfly.data.impl.op.TypedDataOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
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
