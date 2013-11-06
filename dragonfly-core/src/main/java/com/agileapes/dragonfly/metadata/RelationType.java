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

package com.agileapes.dragonfly.metadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 0:11)
 */
public enum RelationType {

    ONE_TO_ONE(1, 1),
    ONE_TO_MANY(1, Integer.MAX_VALUE),
    MANY_TO_ONE(Integer.MAX_VALUE, 1),
    MANY_TO_MANY(Integer.MAX_VALUE, Integer.MAX_VALUE);

    private final int localCardinality;
    private final int foreignCardinality;

    private RelationType(int localCardinality, int foreignCardinality) {
        this.localCardinality = localCardinality;
        this.foreignCardinality = foreignCardinality;
    }

    public int getLocalCardinality() {
        return localCardinality;
    }

    public int getForeignCardinality() {
        return foreignCardinality;
    }

}
