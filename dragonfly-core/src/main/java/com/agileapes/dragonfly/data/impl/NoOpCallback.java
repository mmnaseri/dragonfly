package com.agileapes.dragonfly.data.impl;

import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.DataOperation;

/**
 * This is a callback that will simply let the operation proceed uninterrupted.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 3:32)
 */
public class NoOpCallback implements DataCallback {

    @Override
    public Object execute(DataOperation operation) {
        return operation.proceed();
    }

    @Override
    public boolean accepts(Object o) {
        return true;
    }

}
