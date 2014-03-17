package com.agileapes.dragonfly.analysis.impl;

import com.agileapes.dragonfly.analysis.IssueTarget;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.tools.DatabaseUtils;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 15:36)
 */
public class TableIssueTarget implements IssueTarget<TableMetadata<?>> {

    private final TableMetadata<?> tableMetadata;

    public TableIssueTarget(TableMetadata<?> tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    @Override
    public TableMetadata<?> getTarget() {
        return tableMetadata;
    }

    @Override
    public String toString() {
        return "table " + DatabaseUtils.qualifyTable(tableMetadata, '\'', '.') + " defined for entity '" + tableMetadata.getEntityType().getCanonicalName() + "'";
    }

}
