package com.agileapes.dragonfly.tools;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.metadata.ColumnMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:41)
 */
public class ColumnNameFilter implements Filter<ColumnMetadata> {

    private final String columnName;

    public ColumnNameFilter(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public boolean accepts(ColumnMetadata columnMetadata) {
        return columnName.equalsIgnoreCase(columnMetadata.getName());
    }

}
