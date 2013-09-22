package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.reflection.beans.BeanWrapper;
import com.agileapes.couteau.reflection.beans.impl.MethodBeanWrapper;
import com.agileapes.couteau.reflection.error.NoSuchPropertyException;
import com.agileapes.couteau.reflection.error.PropertyAccessException;
import com.agileapes.couteau.reflection.error.PropertyTypeMismatchException;
import com.agileapes.dragonfly.entity.MapEntityCreator;
import com.agileapes.dragonfly.error.EntityInitializationError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.tools.ColumnNameFilter;

import java.util.Collection;
import java.util.Map;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
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
            } catch (PropertyAccessException e) {
                throw new EntityInitializationError(entity.getClass(), e);
            } catch (PropertyTypeMismatchException e) {
                throw new EntityInitializationError(entity.getClass(), e);
            }
        }
        return entity;
    }
}
