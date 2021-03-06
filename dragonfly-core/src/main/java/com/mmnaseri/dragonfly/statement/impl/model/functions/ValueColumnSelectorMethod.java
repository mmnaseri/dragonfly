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

package com.mmnaseri.dragonfly.statement.impl.model.functions;

import com.mmnaseri.couteau.freemarker.api.Invokable;
import com.mmnaseri.couteau.freemarker.model.FilteringMethodModel;
import com.mmnaseri.couteau.reflection.beans.BeanAccessor;
import com.mmnaseri.couteau.reflection.beans.impl.MethodBeanAccessor;
import com.mmnaseri.couteau.reflection.error.NoSuchPropertyException;
import com.mmnaseri.couteau.reflection.error.PropertyAccessException;
import com.mmnaseri.dragonfly.error.MetadataCollectionError;
import com.mmnaseri.dragonfly.metadata.ColumnMetadata;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Picks out columns for which a value has been provided
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/1, 13:58)
 */
public class ValueColumnSelectorMethod extends FilteringMethodModel<ColumnMetadata> {

    private final Set<String> properties = new HashSet<String>();

    public ValueColumnSelectorMethod(Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Map<?, ?>) {
            //noinspection unchecked
            final Map<String, ?> map = (Map<String, ?>) value;
            for (String property : map.keySet()) {
                if (property.startsWith("value.")) {
                    properties.add(property.substring(6));
                }
            }
            return;
        }
        final BeanAccessor<Object> accessor = new MethodBeanAccessor<Object>(value);
        for (String propertyName : accessor.getPropertyNames()) {
            try {
                final Object propertyValue = accessor.getPropertyValue(propertyName);
                if (propertyValue != null) {
                    properties.add(propertyName);
                }
            } catch (NoSuchPropertyException ignored) {
            } catch (PropertyAccessException e) {
                throw new MetadataCollectionError("Failed to access property " + propertyName + " of entity " + value.getClass().getCanonicalName(), e);
            }
        }
    }


    @Invokable
    @Override
    protected boolean filter(ColumnMetadata columnMetadata) {
        return properties.contains(columnMetadata.getPropertyName());
    }

}
