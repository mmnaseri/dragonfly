package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.tools.SynchronizedIdentifierDispenser;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:28)
 */
public class ForeignKeyConstraintMetadata extends AbstractConstraintMetadata {

    private static SynchronizedIdentifierDispenser<TableMetadata<?>> dispenser = new SynchronizedIdentifierDispenser<TableMetadata<?>>();

    public ForeignKeyConstraintMetadata(TableMetadata table, ColumnMetadata column) {
        super(table, new HashSet<ColumnMetadata>(Arrays.asList(column)));
    }

    public ColumnMetadata getColumn() {
        return getColumns().iterator().next();
    }

    @Override
    protected String getUniqueNameSuffix() {
        return "fk" + dispenser.getIdentifier(getTable());
    }

}
