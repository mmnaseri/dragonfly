package com.agileapes.dragonfly.metadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:07)
 */
public interface ColumnMetadata extends Metadata {

    String getName();

    int getType();

    String getPropertyName();

    Class<?> getPropertyType();

    ColumnMetadata getForeignReference();

    TableMetadata<?> getTable();

    ValueGenerationType getGenerationType();

    String getValueGenerator();

    boolean isNullable();

    int getLength();

    int getPrecision();

    int getScale();

}
