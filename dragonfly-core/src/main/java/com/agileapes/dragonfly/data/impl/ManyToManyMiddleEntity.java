package com.agileapes.dragonfly.data.impl;

import com.agileapes.dragonfly.metadata.ReferenceMetadata;

/**
 * This class is the single implementation used as the middle entity in any many-to-many
 * relation.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/3, 2:06)
 */
public class ManyToManyMiddleEntity {

    private Object first;
    private Object second;
    private ReferenceMetadata<?, ?> referenceMetadata;

    public Object getFirst() {
        return first;
    }

    public void setFirst(Object first) {
        this.first = first;
    }

    public Object getSecond() {
        return second;
    }

    public void setSecond(Object second) {
        this.second = second;
    }

    public boolean isComplete() {
        return first != null && second != null;
    }

    public ReferenceMetadata<?, ?> getReferenceMetadata() {
        return referenceMetadata;
    }

    public void setReferenceMetadata(ReferenceMetadata<?, ?> referenceMetadata) {
        this.referenceMetadata = referenceMetadata;
    }

}
