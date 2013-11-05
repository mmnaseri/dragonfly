package com.agileapes.dragonfly.data.impl;

import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.DataOperation;
import com.agileapes.dragonfly.data.impl.op.SampledDataOperation;
import com.agileapes.dragonfly.data.impl.op.TypedDataOperation;

/**
 * This callback will respond to operations performed on a certain types.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 17:09)
 */
public abstract class TypedDataCallback<O extends DataOperation> implements DataCallback<O> {

    private final Class<?> entityType;

    protected TypedDataCallback(Class<?> entityType) {
        this.entityType = entityType;
    }

    @Override
    public boolean accepts(O item) {
        return item instanceof TypedDataOperation && entityType.isAssignableFrom(((TypedDataOperation) item).getEntityType())
             || item instanceof SampledDataOperation && entityType.isInstance(((SampledDataOperation) item).getSample());
    }
}
