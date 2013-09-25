package com.agileapes.dragonfly.data.impl;

import com.agileapes.dragonfly.data.DataCallback;
import com.agileapes.dragonfly.data.DataOperation;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 3:04)
 */
public abstract class AbstractDefaultDataCallback<E extends DataOperation> implements DataCallback<E> {

    @Override
    public boolean accepts(E e) {
        return true;
    }

}
