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

import com.agileapes.couteau.reflection.beans.BeanAccessor;
import com.agileapes.couteau.reflection.beans.impl.MethodBeanAccessor;
import com.agileapes.couteau.reflection.error.NoSuchPropertyException;
import com.agileapes.couteau.reflection.error.PropertyAccessException;
import com.agileapes.dragonfly.entity.EntityMapCreator;
import com.agileapes.dragonfly.error.EntityDefinitionError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.ConstraintMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.tools.ColumnNameFilter;
import com.agileapes.dragonfly.tools.ColumnPropertyFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * This is the default entity map creator that relies on reflections API and generic assumptions
 * to create maps from entity values. The maps keys are properties.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:43)
 */
public class DefaultEntityMapCreator implements EntityMapCreator {

    private final ObjectMapper mapper = new ObjectMapper();

    public DefaultEntityMapCreator() {
        mapper.enableDefaultTyping();
    }

    @Override
    public <E> Map<String, Object> toMap(TableMetadata<E> tableMetadata, E entity) {
        return toMap(tableMetadata.getColumns(), entity);
    }

    @Override
    public <E> Map<String, Object> toMap(Collection<ColumnMetadata> columns, E entity) {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        final BeanAccessor<E> accessor = new MethodBeanAccessor<E>(entity);
        for (String propertyName : accessor.getPropertyNames()) {
            ColumnMetadata column = with(columns).keep(new ColumnPropertyFilter(propertyName)).first();
            if (column == null) {
                continue;
            }
            Object value;
            try {
                value = accessor.getPropertyValue(propertyName);
            } catch (NoSuchPropertyException ignored) {
                //this won't happen
                continue;
            } catch (PropertyAccessException e) {
                throw new RuntimeException(e);
            }
            if (value == null) {
                continue;
            }
            if (column.getForeignReference() != null) {
                final ColumnMetadata target;
                if (column.getForeignReference().getName() == null || column.getForeignReference().getName().isEmpty()) {
                    final ConstraintMetadata primaryKey = column.getForeignReference().getTable().getPrimaryKey();
                    if (primaryKey == null) {
                        throw new RuntimeException("Entity " + entity.getClass().getCanonicalName() + " references a non-existent primary key in " + column.getName());
                    }
                    target = primaryKey.getColumns().iterator().next();
                } else {
                    target = with(column.getForeignReference().getTable().getColumns()).keep(new ColumnNameFilter(column.getForeignReference().getName())).first();
                    if (target == null) {
                        throw new RuntimeException("Entity " + entity.getClass().getCanonicalName() + " references a non-existent column in " + column.getName());
                    }
                }
                if (!target.getDeclaringClass().isInstance(value)) {
                    continue;
                }
                final BeanAccessor<?> targetAccessor = new MethodBeanAccessor<Object>(value);
                try {
                    value = targetAccessor.getPropertyValue(target.getPropertyName());
                } catch (NoSuchPropertyException e) {
                    throw new RuntimeException("Property not found", e);
                } catch (PropertyAccessException e) {
                    throw new RuntimeException("Error accessing property", e);
                }
                column = column.getForeignReference();
            }
            if (value instanceof Date) {
                if (column.getType() == Types.DATE) {
                    value = new java.sql.Date(((Date) value).getTime());
                } else if (column.getType() == Types.TIME) {
                    value = new Time(((Date) value).getTime());
                } else {
                    //we will assume that it is Types.TIMESTAMP
                    value = new Timestamp(((Date) value).getTime());
                }
            } else if (value instanceof Class<?>) {
                value = ((Class<?>) value).getCanonicalName();
            } else if (column.isCollection()) {
                final StringBuilder builder = new StringBuilder();
                builder.append('[');
                if (!(value instanceof Collection<?>)) {
                    throw new EntityDefinitionError("Expected a collection but found " + value);
                }
                Collection<?> collection = (Collection<?>) value;
                boolean first = true;
                for (Object item : collection) {
                    if (!first) {
                        builder.append(",");
                    }
                    first = false;
                    if (item instanceof Date) {
                        builder.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format((Date) item));
                    } else if (item instanceof Enum) {
                        builder.append(((Enum) item).name());
                    } else if (item instanceof Class<?>) {
                        builder.append(((Class<?>) item).getCanonicalName());
                    } else if (item instanceof File) {
                        builder.append(((File) item).getAbsolutePath());
                    } else {
                        builder.append(item.toString());
                    }
                }
                builder.append(']');
                value = builder.toString();
            } else if (column.isComplex()) {
                try {
                    value = value.getClass().getCanonicalName() + ";" + mapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new EntityDefinitionError("Failed to convert data value to JSON", e);
                }
            }
            map.put(propertyName, value);
        }
        return map;
    }

}
