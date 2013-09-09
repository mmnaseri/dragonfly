package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.annotations.ParameterMode;
import com.agileapes.dragonfly.metadata.ParameterMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 0:49)
 */
public class ImmutableParameterMetadata implements ParameterMetadata {

    private final ParameterMode parameterMode;
    private final int type;
    private final Class<?> parameterType;

    public ImmutableParameterMetadata(ParameterMode parameterMode, int type, Class<?> parameterType) {
        this.parameterMode = parameterMode;
        this.type = type;
        this.parameterType = parameterType;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public Class<?> getParameterType() {
        return parameterType;
    }

    @Override
    public ParameterMode getParameterMode() {
        return parameterMode;
    }
}
