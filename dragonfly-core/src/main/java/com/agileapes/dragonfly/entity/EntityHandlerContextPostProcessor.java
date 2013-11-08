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

package com.agileapes.dragonfly.entity;

/**
 * This interface allows for post-processing of the entity handler context after it is
 * (auto-)populated
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/18, 12:40)
 */
public interface EntityHandlerContextPostProcessor {

    /**
     * Will be called to post-process the handler context
     * @param entityHandlerContext    the handler context to be post-processed
     */
    void postProcessEntityHandlerContext(EntityHandlerContext entityHandlerContext);

}
