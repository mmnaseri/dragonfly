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

package com.mmnaseri.dragonfly.data.impl;

import com.mmnaseri.dragonfly.data.DataCallback;
import com.mmnaseri.dragonfly.data.DataOperation;
import com.mmnaseri.dragonfly.data.impl.op.SampledDataOperation;
import com.mmnaseri.dragonfly.data.impl.op.TypedDataOperation;

/**
 * This callback will respond to operations performed on a certain types.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/26, 17:09)
 */
public abstract class TypedDataCallback<O extends DataOperation> implements DataCallback<O> {

    private final Class<?> entityType;

    protected TypedDataCallback(Class<?> entityType) {
        this.entityType = entityType;
    }

    @Override
    public boolean accepts(O item) {
        return item instanceof TypedDataOperation && entityType.isAssignableFrom(((TypedDataOperation) item).getEntityType())
             || item instanceof SampledDataOperation && entityType.isInstance(((SampledDataOperation) item).getSample());
    }
}
