package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.reflection.beans.BeanAccessor;
import com.agileapes.couteau.reflection.beans.impl.MethodBeanAccessor;
import com.agileapes.couteau.reflection.error.NoSuchPropertyException;
import com.agileapes.couteau.reflection.error.PropertyAccessException;
import com.agileapes.dragonfly.entity.EntityMapCreator;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.ConstraintMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.tools.ColumnNameFilter;
import com.agileapes.dragonfly.tools.ColumnPropertyFilter;

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:43)
 */
public class DefaultEntityMapCreator implements EntityMapCreator {

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
                throw new Error(e);
            }
            if (value == null) {
                continue;
            }
            if (column.getForeignReference() != null) {
                final ColumnMetadata target;
                if (column.getForeignReference().getName() == null || column.getForeignReference().getName().isEmpty()) {
                    final ConstraintMetadata primaryKey = column.getForeignReference().getTable().getPrimaryKey();
                    if (primaryKey == null) {
                        throw new Error("Entity " + entity.getClass().getCanonicalName() + " references a non-existent primary key in " + column.getName());
                    }
                    target = primaryKey.getColumns().iterator().next();
                } else {
                    target = with(column.getForeignReference().getTable().getColumns()).keep(new ColumnNameFilter(column.getForeignReference().getName())).first();
                    if (target == null) {
                        throw new Error("Entity " + entity.getClass().getCanonicalName() + " references a non-existent column in " + column.getName());
                    }
                }
                final BeanAccessor<?> targetAccessor = new MethodBeanAccessor<Object>(value);
                try {
                    value = targetAccessor.getPropertyValue(target.getPropertyName());
                } catch (NoSuchPropertyException e) {
                    throw new Error("Property not found", e);
                } catch (PropertyAccessException e) {
                    throw new Error("Error accessing property", e);
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
            }
            map.put(propertyName, value);
        }
        return map;
    }

}
