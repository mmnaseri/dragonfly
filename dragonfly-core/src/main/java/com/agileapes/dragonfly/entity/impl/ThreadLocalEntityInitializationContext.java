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

import com.agileapes.couteau.basics.api.Cache;
import com.agileapes.couteau.basics.api.impl.AbstractCache;
import com.agileapes.couteau.basics.api.impl.SimpleCache;
import com.agileapes.dragonfly.data.DataAccess;

/**
 * This entity initialization instance is an instance that can not be nested inside another
 * context, and as such will always be the parent in its context tree. It is also thread-local
 * which means that it will expose a different cache instance for each thread.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/21, 13:45)
 */
public class ThreadLocalEntityInitializationContext extends AbstractLockingEntityInitializationContext {

    private final ThreadLocal<Cache<EntityInstanceDescriptor, Object>> threadLocalCache = new ThreadLocal<Cache<EntityInstanceDescriptor, Object>>() {
        @Override
        protected Cache<EntityInstanceDescriptor, Object> initialValue() {
            return new SimpleCache<EntityInstanceDescriptor, Object>(100, AbstractCache.RemovePolicy.OLDEST_FIRST);
        }
    };

    public ThreadLocalEntityInitializationContext(DataAccess dataAccess) {
        super(dataAccess, null);
    }

    @Override
    protected synchronized Cache<EntityInstanceDescriptor, Object> getCache() {
        return threadLocalCache.get();
    }
}
