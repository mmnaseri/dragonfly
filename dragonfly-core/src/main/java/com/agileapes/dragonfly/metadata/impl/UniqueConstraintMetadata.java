package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.tools.SynchronizedIdentifierDispenser;

import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:12)
 */
public class UniqueConstraintMetadata extends AbstractConstraintMetadata {

    private static SynchronizedIdentifierDispenser<TableMetadata<?>> dispenser = new SynchronizedIdentifierDispenser<TableMetadata<?>>();

    public UniqueConstraintMetadata(TableMetadata table, Collection<ColumnMetadata> columns) {
        super(table, columns);
    }

    @Override
    protected String getNameSuffix() {
        return "uk" + dispenser.getIdentifier(getTable());
    }
}
