package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/30, 3:14)
 */
public class ResolvedColumnMetadata extends AbstractColumnMetadata {

    private final Class<?> type;
    private final String propertyName;
    private final Class<?> propertyType;
    private final ColumnMetadata foreignReference;

    public ResolvedColumnMetadata(TableMetadata table, String name, Class<?> type, String propertyName, Class<?> propertyType) {
        this(table, name, type, propertyName, propertyType, null);
    }

    public ResolvedColumnMetadata(TableMetadata table, String name, Class<?> type, String propertyName, Class<?> propertyType, ColumnMetadata foreignReference) {
        super(name, table);
        this.type = type;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.foreignReference = foreignReference;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public Class<?> getPropertyType() {
        return propertyType;
    }

    @Override
    public ColumnMetadata getForeignReference() {
        return foreignReference;
    }

}
