package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.OrderMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/30, 11:28)
 */
public class ImmutableOrderMetadata implements OrderMetadata {

    private final ColumnMetadata column;
    private final String order;

    public ImmutableOrderMetadata(ColumnMetadata column, String order) {
        this.column = column;
        this.order = order;
    }

    @Override
    public ColumnMetadata getColumn() {
        return column;
    }

    @Override
    public String getOrder() {
        return order;
    }
}
