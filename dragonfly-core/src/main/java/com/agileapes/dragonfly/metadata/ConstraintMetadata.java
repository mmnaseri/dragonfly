package com.agileapes.dragonfly.metadata;

import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:09)
 */
public interface ConstraintMetadata extends Metadata {

    String getName();

    TableMetadata getTable();

    Collection<ColumnMetadata> getColumns();

}
