package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.ConstraintMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.tools.DatabaseUtils;

import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:11)
 */
public abstract class AbstractConstraintMetadata implements ConstraintMetadata {

    private final String name;
    private final TableMetadata table;
    private final Collection<ColumnMetadata> columns;

    public AbstractConstraintMetadata(TableMetadata table, Collection<ColumnMetadata> columns) {
        this.name = produceName();
        this.table = table;
        this.columns = columns;
    }

    private String produceName() {
        return DatabaseUtils.unify(getTable().getName()) + "_" + getUniqueNameSuffix();
    }

    protected abstract String getUniqueNameSuffix();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TableMetadata getTable() {
        return table;
    }

    @Override
    public Collection<ColumnMetadata> getColumns() {
        return columns;
    }
}
