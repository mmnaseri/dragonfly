package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.SequenceMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 17:22)
 */
public class DefaultSequenceMetadata implements SequenceMetadata {

    private final String name;
    private final int initialValue;
    private final int prefetchSize;

    public DefaultSequenceMetadata(String name, int initialValue, int prefetchSize) {
        this.name = name;
        this.initialValue = initialValue;
        this.prefetchSize = prefetchSize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getInitialValue() {
        return initialValue;
    }

    @Override
    public int getPrefetchSize() {
        return prefetchSize;
    }

}
