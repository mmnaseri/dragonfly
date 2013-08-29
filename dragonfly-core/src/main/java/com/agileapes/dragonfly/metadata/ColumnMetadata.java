package com.agileapes.dragonfly.metadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:07)
 */
public interface ColumnMetadata {

    String getName();

    Class<?> getType();

    String getPropertyName();

    Class<?> getPropertyType();

    ColumnMetadata getForeignReference();

    TableMetadata getTable();

}
