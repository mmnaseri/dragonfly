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
 * This interface encapsulates all the necessary properties of a sequence.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 16:27)
 */
public interface SequenceMetadata extends Metadata {

    /**
     * @return the name of the sequence, which must be unique across the persistence context
     */
    String getName();

    /**
     * @return the initial value of the sequence, as seen by the application
     */
    int getInitialValue();

    /**
     * @return number of items to prefetch to reduce database access. Too high a number might
     * result in inconsistency or extraneous usage of the memory
     */
    int getPrefetchSize();

}
