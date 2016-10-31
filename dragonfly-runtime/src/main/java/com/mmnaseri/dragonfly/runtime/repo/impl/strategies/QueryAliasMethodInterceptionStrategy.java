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
import com.mmnaseri.dragonfly.entity.EntityHandler;
import com.mmnaseri.dragonfly.runtime.repo.QueryAlias;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/9/3 AD, 14:39)
 */
public class QueryAliasMethodInterceptionStrategy extends AbstractQueryingMethodInterceptionStrategy {

    public QueryAliasMethodInterceptionStrategy(Class entityType, DataAccess dataAccess, EntityHandler entityHandler) {
        super(entityType, dataAccess, entityHandler);
    }

    @Override
    public boolean accepts(Method method) {
        return method.isAnnotationPresent(QueryAlias.class) && (method.getReturnType().equals(void.class) || method.getReturnType().isAssignableFrom(getEntityType()) || List.class.isAssignableFrom(method.getReturnType()));
    }

    @Override
    protected String getQueryName(Method method) {
        return method.getAnnotation(QueryAlias.class).value();
    }

}
