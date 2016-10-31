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

import com.mmnaseri.couteau.context.error.RegistryException;
import com.mmnaseri.couteau.context.value.ValueReaderContext;
import com.mmnaseri.couteau.context.value.impl.*;
import com.mmnaseri.couteau.reflection.beans.BeanWrapper;
import com.mmnaseri.couteau.reflection.beans.impl.MethodBeanWrapper;
import com.mmnaseri.couteau.reflection.error.NoSuchPropertyException;
import com.mmnaseri.couteau.reflection.util.ClassUtils;
import com.mmnaseri.dragonfly.entity.MapEntityCreator;
import com.mmnaseri.dragonfly.error.EntityDefinitionError;
import com.mmnaseri.dragonfly.error.EntityInitializationError;
import com.mmnaseri.dragonfly.metadata.ColumnMetadata;
import com.mmnaseri.dragonfly.tools.ColumnNameFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * This class will use column metadata for each entity to fill an entity instance's properties
 * with values put into the map. The map's keys are assumed to be column names.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/8/31, 17:39)
 */
public class DefaultMapEntityCreator implements MapEntityCreator {

    private static ValueReaderContext getValueReaderContext() throws RegistryException {
        final ClassValueReader item = new ClassValueReader();
        item.setClassLoader(DefaultMapEntityCreator.class.getClassLoader());
        return (ValueReaderContext)
                new DefaultValueReaderContext()
                        .register(item)
                        .register(new DateValueReader())
                        .register(new EnumValueReader())
                        .register(new FileValueReader())
                        .register(new PrimitiveValueReader())
                        .register(new UrlValueReader());
    }

    private final ValueReaderContext readerContext;

    private final ObjectMapper mapper = new ObjectMapper();

    public DefaultMapEntityCreator() throws RegistryException {
        this(getValueReaderContext());
    }

    public DefaultMapEntityCreator(ValueReaderContext readerContext) {
        this.readerContext = readerContext;
        this.mapper.enableDefaultTyping();
    }

    @Override
    public <E> E fromMap(E entity, Collection<ColumnMetadata> columns, Map<String, Object> values) {
        final BeanWrapper<E> wrapper = new MethodBeanWrapper<E>(entity);
        for (Map.Entry<String, Object> value : values.entrySet()) {
            if (value.getValue() == null) {
                continue;
            }
            final ColumnMetadata columnMetadata = with(columns).keep(new ColumnNameFilter(value.getKey())).first();
            if (columnMetadata == null) {
                //as of this moment, we have chosen to ignore column clashes between
                //the map and the entity
                continue;
            }
            try {
                Object inferredValue = value.getValue();
                if (inferredValue == null) {
                    continue;
                }
                if (columnMetadata.isComplex() && inferredValue instanceof String) {
                    final String[] split = ((String) inferredValue).split(";", 2);
                    final Class targetType = ClassUtils.forName(split[0], getClass().getClassLoader());
                    inferredValue = mapper.readValue(split[1], targetType);
                }
                final Class propertyType = columnMetadata.getPropertyType();
                if (Enum.class.isAssignableFrom(propertyType)) {
                    if (!(inferredValue instanceof String)) {
                        throw new IllegalArgumentException("Expected retrieved value for an enum to be a string");
                    }
                    inferredValue = Enum.valueOf(propertyType, (String) inferredValue);
                } else if (Character.class.isAssignableFrom(propertyType) && inferredValue instanceof String && ((String) inferredValue).length() == 1) {
                    inferredValue = ((String) inferredValue).charAt(0);
                } else if (Class.class.isAssignableFrom(propertyType) && inferredValue instanceof String && !((String) inferredValue).isEmpty()) {
                    inferredValue = ClassUtils.forName((String) inferredValue, getClass().getClassLoader());
                } else if (columnMetadata.isCollection()) {
                    if (!Collection.class.isAssignableFrom(propertyType)) {
                        throw new EntityDefinitionError("Expected property `" + columnMetadata.getPropertyName() + "` to be a collection but it was " + propertyType.getCanonicalName());
                    }
                    if (!(inferredValue instanceof String)) {
                        throw new EntityDefinitionError("Invalid property value for basic collection " + columnMetadata.getPropertyName());
                    }
                    final Type type = wrapper.getGenericPropertyType(columnMetadata.getPropertyName());
                    inferredValue = readerContext.read((String) inferredValue, type);
                }
                wrapper.setPropertyValue(columnMetadata.getPropertyName(), inferredValue);
            } catch (NoSuchPropertyException e) {
                //ditto here
            } catch (Exception e) {
                throw new EntityInitializationError(entity.getClass(), e);
            }
        }
        return entity;
    }
}
