package com.agileapes.dragonfly.metadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 16:27)
 */
public interface SequenceMetadata extends Metadata {

    String getName();

    int getInitialValue();

    int getPrefetchSize();

}
