/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.data.impl;

import com.agileapes.dragonfly.metadata.RelationMetadata;

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
    private RelationMetadata<?, ?> relationMetadata;

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

    public RelationMetadata<?, ?> getRelationMetadata() {
        return relationMetadata;
    }

    public void setRelationMetadata(RelationMetadata<?, ?> relationMetadata) {
        this.relationMetadata = relationMetadata;
    }

}
