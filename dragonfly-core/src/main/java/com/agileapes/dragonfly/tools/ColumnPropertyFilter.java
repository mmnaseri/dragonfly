package com.agileapes.dragonfly.tools;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.metadata.ColumnMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:47)
 */
public class ColumnPropertyFilter implements Filter<ColumnMetadata> {

    private final String propertyName;

    public ColumnPropertyFilter(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public boolean accepts(ColumnMetadata columnMetadata) {
        return propertyName.equals(columnMetadata.getPropertyName());
    }

}
