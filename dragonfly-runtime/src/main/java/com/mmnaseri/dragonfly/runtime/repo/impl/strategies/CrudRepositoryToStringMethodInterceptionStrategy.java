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

import com.mmnaseri.dragonfly.runtime.repo.MethodInterceptionStrategy;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/21 AD, 12:36)
 */
public class CrudRepositoryToStringMethodInterceptionStrategy implements MethodInterceptionStrategy {

    private final Class repository;
    private final Class entityType;
    private final Class keyType;

    public CrudRepositoryToStringMethodInterceptionStrategy(Class repository, Class entityType, Class keyType) {
        this.repository = repository;
        this.entityType = entityType;
        this.keyType = keyType;
    }

    @Override
    public boolean accepts(Method item) {
        return item.getName().equals("toString") && item.getParameterTypes().length == 0 && item.getReturnType().equals(String.class);
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return repository.getSimpleName() + "<" + entityType.getSimpleName() + "," + keyType.getSimpleName() + ">";
    }

}
