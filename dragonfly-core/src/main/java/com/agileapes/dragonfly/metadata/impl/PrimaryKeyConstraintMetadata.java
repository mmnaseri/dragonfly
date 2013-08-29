package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.tools.SynchronizedIdentifierDispenser;

import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:34)
 */
public class PrimaryKeyConstraintMetadata extends AbstractConstraintMetadata {

    private static SynchronizedIdentifierDispenser<TableMetadata<?>> dispenser = new SynchronizedIdentifierDispenser<TableMetadata<?>>();

    public PrimaryKeyConstraintMetadata(TableMetadata table, Collection<ColumnMetadata> columns) {
        super(table, columns);
    }

    @Override
    protected String getUniqueNameSuffix() {
        return "pk" + dispenser.getIdentifier(getTable());
    }

}
