package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 18:05)
 */
public abstract class AbstractColumnMetadata implements ColumnMetadata {

    private final String name;
    private final TableMetadata table;

    public AbstractColumnMetadata(String name, TableMetadata table) {
        this.name = name;
        this.table = table;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TableMetadata getTable() {
        return table;
    }

}
