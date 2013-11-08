/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.reflection.beans.BeanWrapper;
import com.agileapes.couteau.reflection.beans.impl.MethodBeanWrapper;
import com.agileapes.couteau.reflection.error.NoSuchPropertyException;
import com.agileapes.dragonfly.entity.MapEntityCreator;
import com.agileapes.dragonfly.error.EntityInitializationError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.tools.ColumnNameFilter;

import java.util.Collection;
import java.util.Map;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * This class will use column metadata for each entity to fill an entity instance's properties
 * with values put into the map. The map's keys are assumed to be column names.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:39)
 */
public class DefaultMapEntityCreator implements MapEntityCreator {

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
                wrapper.setPropertyValue(columnMetadata.getPropertyName(), value.getValue());
            } catch (NoSuchPropertyException e) {
                //ditto here
            } catch (Exception e) {
                throw new EntityInitializationError(entity.getClass(), e);
            }
        }
        return entity;
    }
}
