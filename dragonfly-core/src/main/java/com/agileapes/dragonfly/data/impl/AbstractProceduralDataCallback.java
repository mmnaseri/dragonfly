package com.agileapes.dragonfly.data.impl;

import com.agileapes.dragonfly.data.DataOperation;

/**
 * This is an operation that is supposed to not return any values
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 3:02)
 */
public abstract class AbstractProceduralDataCallback<E extends DataOperation> extends AbstractDefaultDataCallback<E> {

    protected abstract void executeWithoutResults(E operation);

    @Override
    public Object execute(E operation) {
        executeWithoutResults(operation);
        return null;
    }

}
