package com.agileapes.dragonfly.metadata;

import java.sql.Types;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:07)
 */
public interface ColumnMetadata {

    String getName();

    int getType();

    String getPropertyName();

    Class<?> getPropertyType();

    ColumnMetadata getForeignReference();

    TableMetadata<?> getTable();

    ValueGenerationType getGenerationType();

    String getValueGenerator();

}
