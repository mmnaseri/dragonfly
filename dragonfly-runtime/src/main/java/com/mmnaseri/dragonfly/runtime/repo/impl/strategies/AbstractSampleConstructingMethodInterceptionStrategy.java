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

import com.mmnaseri.couteau.reflection.beans.BeanWrapper;
import com.mmnaseri.couteau.reflection.beans.impl.MethodBeanWrapper;
import com.mmnaseri.couteau.strings.document.DocumentReader;
import com.mmnaseri.couteau.strings.document.impl.DefaultDocumentReader;
import com.mmnaseri.dragonfly.runtime.repo.MethodInterceptionStrategy;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/21 AD, 12:39)
 */
public abstract class AbstractSampleConstructingMethodInterceptionStrategy implements MethodInterceptionStrategy {

    private final String prefix;
    private final Class entityType;

    public AbstractSampleConstructingMethodInterceptionStrategy(String prefix, Class entityType) {
        this.prefix = prefix;
        this.entityType = entityType;
    }

    @Override
    public Object intercept(Object repository, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        return intercept(repository, method, arguments, methodProxy, constructSample(method, arguments));
    }

    protected Object constructSample(Method method, Object[] arguments) throws Exception{
        final Object sample = entityType.newInstance();
        final DocumentReader reader = new DefaultDocumentReader(method.getName().substring(prefix.length()));
        final BeanWrapper<Object> wrapper = new MethodBeanWrapper<Object>(sample);
        final Map<String, Object> values = new HashMap<String, Object>();
        int i = 0;
        while (reader.hasMore()) {
            for (String property : wrapper.getPropertyNames()) {
                final String capitalized = StringUtils.capitalize(property);
                if (reader.has(capitalized)) {
                    reader.read(capitalized, false);
                    values.put(property, arguments[i ++]);
                    break;
                }
            }
            try {
                reader.expect("And|$", false);
            } catch (Exception e) {
                throw new IllegalStateException("Expected to see an AND operator or the end of the string, but found " + reader.rest());
            }
        }
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            final String propertyName = entry.getKey();
            final Class<?> propertyType = wrapper.getPropertyType(propertyName);
            final Object propertyValue = entry.getValue();
            if (!propertyType.isInstance(propertyValue)) {
                throw new IllegalArgumentException("Property " + propertyName + " expects values of type " + propertyType.getCanonicalName());
            }
            wrapper.setPropertyValue(propertyName, propertyValue);
        }
        return sample;
    }

    protected abstract Object intercept(Object repository, Method method, Object[] arguments, MethodProxy methodProxy, Object sample);

}
