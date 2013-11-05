package com.agileapes.dragonfly.data.impl;

import com.agileapes.couteau.reflection.util.ClassUtils;
import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.DataOperation;

/**
 * This data callback allows for easy determination of the operation types through generics
 * of the data callback passed through.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 2:44)
 */
public class SmartDataCallback<E extends DataOperation> implements DataCallback<E> {

    private final DataCallback<E> callback;
    private final Class<? extends DataOperation> operationType;

    public SmartDataCallback(DataCallback<E> callback) {
        this.callback = callback;
        final Class<?> typeArgument = ClassUtils.resolveTypeArgument(callback.getClass(), DataCallback.class);
        this.operationType = (typeArgument == null ? DataOperation.class : typeArgument).asSubclass(DataOperation.class);
    }

    @Override
    public Object execute(E operation) {
        return callback.execute(operation);
    }

    @Override
    public boolean accepts(E dataOperation) {
        return operationType.isAssignableFrom(dataOperation.getClass()) && callback.accepts(dataOperation);
    }

}
