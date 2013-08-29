package com.agileapes.dragonfly.error;

import com.agileapes.dragonfly.metadata.TableMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:38)
 */
public class NoPrimaryKeyDefinedError extends DatabaseError {

    private final TableMetadata<?> table;

    public NoPrimaryKeyDefinedError(TableMetadata<?> table) {
        super("No primary key has been defined for: " + table.getQualifiedName());
        this.table = table;
    }

    public TableMetadata<?> getTable() {
        return table;
    }

}
