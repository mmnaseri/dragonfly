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

package com.agileapes.dragonfly.runtime.repo.impl.strategies;

import com.mmnaseri.couteau.basics.api.Filter;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.entity.EntityHandler;
import com.agileapes.dragonfly.error.QueryDefinitionError;
import com.agileapes.dragonfly.runtime.repo.MethodInterceptionStrategy;
import com.agileapes.dragonfly.runtime.repo.Parameter;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/9/3 AD, 14:47)
 */
public abstract class AbstractQueryingMethodInterceptionStrategy implements MethodInterceptionStrategy {

    private final Class entityType;
    private final DataAccess dataAccess;
    private final EntityHandler entityHandler;

    protected AbstractQueryingMethodInterceptionStrategy(Class entityType, DataAccess dataAccess, EntityHandler entityHandler) {
        this.entityType = entityType;
        this.dataAccess = dataAccess;
        this.entityHandler = entityHandler;
    }

    protected Class getEntityType() {
        return entityType;
    }

    @Override
    public Object intercept(Object repository, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        final HashMap<String, Object> values = new HashMap<String, Object>();
        //noinspection unchecked
        if (method.getParameterTypes().length == 1 && entityType.isAssignableFrom(method.getParameterTypes()[0])) {
            //noinspection unchecked
            values.putAll(entityHandler.toMap(arguments[0]));
        } else {
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] annotations = parameterAnnotations[i];
                final Parameter parameter = (Parameter) with(annotations).find(new Filter<Annotation>() {
                    @Override
                    public boolean accepts(Annotation item) {
                        return Parameter.class.equals(item.annotationType());
                    }
                });
                values.put(parameter.value(), arguments[i]);
            }
        }
        final String queryName = getQueryName(method);
        if (method.getReturnType().equals(void.class)) {
            dataAccess.executeUpdate(entityType, queryName, values);
            return null;
        }
        final List list = dataAccess.executeQuery(entityType, queryName, values);
        if (method.getReturnType().isAssignableFrom(entityType)) {
            if (list.isEmpty()) {
                return null;
            } else if (list.size() > 1) {
                throw new QueryDefinitionError(entityType, queryName, "Expected query to return one item but it returned " + list.size());
            }
            return list.get(0);
        }
        return list;
    }

    protected abstract String getQueryName(Method method);

}
