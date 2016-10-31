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

import com.mmnaseri.couteau.basics.api.Cache;
import com.mmnaseri.couteau.basics.api.impl.ConcurrentCache;
import com.mmnaseri.dragonfly.data.DataAccess;
import com.mmnaseri.dragonfly.entity.EntityInitializationContext;

/**
 * This is a default initialization context that does not concern itself with any sort of thread
 * consideration save protecting its cache against multiple thread write accesses.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/20, 16:39)
 */
public class DefaultEntityInitializationContext extends AbstractLockingEntityInitializationContext {

    private final Cache<EntityInstanceDescriptor, Object> cache = new ConcurrentCache<EntityInstanceDescriptor, Object>();

    public DefaultEntityInitializationContext(DataAccess dataAccess, EntityInitializationContext parent) {
        super(dataAccess, parent);
    }

    @Override
    protected Cache<EntityInstanceDescriptor, Object> getCache() {
        return cache;
    }
}
