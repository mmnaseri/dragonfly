package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.error.MetadataCollectionError;
import com.agileapes.dragonfly.error.UnresolvedTableMetadataError;
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

    private String name;
    private TableMetadata table;
    private final Collection<ColumnMetadata> columns;

    public AbstractConstraintMetadata(TableMetadata table, Collection<ColumnMetadata> columns) {
        this.columns = columns;
        setTable(table);
    }

    private String produceName() {
        return DatabaseUtils.unify(getTable().getName()) + "_" + getNameSuffix();
    }

    protected abstract String getNameSuffix();

    @Override
    public String getName() {
        if (name == null) {
            throw new MetadataCollectionError("Constraint name not available", new UnresolvedTableMetadataError());
        }
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

    public void setTable(TableMetadata<?> table) {
        this.table = table;
        if (table != null) {
            this.name = produceName();
        }
    }

}
