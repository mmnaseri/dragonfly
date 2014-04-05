package com.agileapes.dragonfly.analysis.impl;

import com.agileapes.dragonfly.analysis.IssueTarget;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.tools.DatabaseUtils;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 15:44)
 */
public class ColumnIssueTarget implements IssueTarget<ColumnMetadata> {

    private final ColumnMetadata columnMetadata;

    public ColumnIssueTarget(ColumnMetadata columnMetadata) {
        this.columnMetadata = columnMetadata;
    }

    @Override
    public ColumnMetadata getTarget() {
        return columnMetadata;
    }

    @Override
    public String toString() {
        return "column " + DatabaseUtils.qualifyTable(columnMetadata.getTable(), '\'', '.') + ".'" + columnMetadata.getName() + "'";
    }

}
