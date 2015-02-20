/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.EntityHandler;
import com.agileapes.dragonfly.entity.EntityHandlerContext;
import com.agileapes.dragonfly.entity.EntityHandlerContextPostProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is able to take a list of entity handlers and feed them to the entity handler context
 * configured for the application. This will override the use of predesignated {@link GenericEntityHandler}
 * instances for some entity types.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 5:50)
 */
public class HandlerContextPreparatorPostProcessor implements EntityHandlerContextPostProcessor {

    private Set<EntityHandler<?>> handlers = new HashSet<EntityHandler<?>>();

    public void setHandlers(Set<EntityHandler<?>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void postProcessEntityHandlerContext(EntityHandlerContext entityHandlerContext) {
        for (EntityHandler<?> mapHandler : handlers) {
            entityHandlerContext.addHandler(mapHandler);
        }
    }
}
