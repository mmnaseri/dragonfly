/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
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

package com.mmnaseri.dragonfly.entity.impl;

import com.mmnaseri.dragonfly.entity.EntityHandler;
import com.mmnaseri.dragonfly.entity.MapEntityCreator;
import com.mmnaseri.dragonfly.metadata.ColumnMetadata;

import java.util.Collection;
import java.util.Map;

/**
 * This class will take an entity handler and delegate creation of entity maps to the handler
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/14, 5:35)
 */
public class DelegatingEntityCreator implements MapEntityCreator {
    
    private final EntityHandler<Object> handler;

    public DelegatingEntityCreator(EntityHandler<Object> handler) {
        this.handler = handler;
    }

    @Override
    public <E> E fromMap(E entity, Collection<ColumnMetadata> columns, Map<String, Object> values) {
        if (!handler.getEntityType().isInstance(entity)) {
            throw new UnsupportedOperationException();
        }
        //noinspection unchecked
        return (E) handler.fromMap(entity, values);
    }
}
