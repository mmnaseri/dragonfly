package com.agileapes.dragonfly.query.impl.functions;

import com.agileapes.couteau.freemarker.api.Invokable;
import com.agileapes.couteau.freemarker.model.FilteringMethodModel;
import com.agileapes.couteau.reflection.beans.BeanAccessor;
import com.agileapes.couteau.reflection.beans.impl.MethodBeanAccessor;
import com.agileapes.couteau.reflection.error.NoSuchPropertyException;
import com.agileapes.couteau.reflection.error.PropertyAccessException;
import com.agileapes.dragonfly.error.MetadataCollectionError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/1, 13:58)
 */
public class ValueColumnSelectorMethod extends FilteringMethodModel<ColumnMetadata> {

    private final Set<String> properties = new HashSet<String>();

    public ValueColumnSelectorMethod(Object value) {
        if (value == null) {
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