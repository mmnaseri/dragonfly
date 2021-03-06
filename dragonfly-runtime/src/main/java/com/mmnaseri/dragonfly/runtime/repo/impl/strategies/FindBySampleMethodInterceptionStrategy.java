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

package com.mmnaseri.dragonfly.runtime.repo.impl.strategies;

import com.mmnaseri.dragonfly.data.DataAccess;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/21 AD, 11:56)
 */
public class FindBySampleMethodInterceptionStrategy extends AbstractSampleConstructingMethodInterceptionStrategy {

    public static final String PREFIX = "findBy";
    private final Class entityType;
    private final DataAccess dataAccess;

    public FindBySampleMethodInterceptionStrategy(Class entityType, DataAccess dataAccess) {
        super(PREFIX, entityType);
        this.entityType = entityType;
        this.dataAccess = dataAccess;
    }

    @Override
    public boolean accepts(Method item) {
        //noinspection unchecked
        return item.getName().startsWith(PREFIX) && (List.class.isAssignableFrom(item.getReturnType()) || entityType.isAssignableFrom(item.getReturnType()));
    }

    @Override
    protected Object intercept(Object repository, Method method, Object[] arguments, MethodProxy methodProxy, Object sample) {
        final List<Object> found = dataAccess.find(sample);
        //noinspection unchecked
        if (entityType.isAssignableFrom(method.getReturnType())) {
            if (found.isEmpty()) {
                return null;
            }
            if (found.size() > 1) {
                throw new IllegalStateException("Expected query to return only one item but it returned " + found.size());
            }
            return found.get(0);
        }
        return found;
    }

}
