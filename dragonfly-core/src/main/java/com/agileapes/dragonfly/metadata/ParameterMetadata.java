package com.agileapes.dragonfly.metadata;

import com.agileapes.dragonfly.annotations.ParameterMode;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 0:46)
 */
public interface ParameterMetadata extends Metadata {

    int getType();

    Class<?> getParameterType();

    ParameterMode getParameterMode();

}
