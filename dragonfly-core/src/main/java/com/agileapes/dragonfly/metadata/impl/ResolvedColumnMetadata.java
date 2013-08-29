package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.ValueGenerationType;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/30, 3:14)
 */
public class ResolvedColumnMetadata extends AbstractColumnMetadata {

    private final Class<?> type;
    private final String propertyName;
    private final Class<?> propertyType;
    private final ColumnMetadata foreignReference;
    private final ValueGenerationType generationType;
    private final String valueGenerator;

    public ResolvedColumnMetadata(TableMetadata table, String name, Class<?> type, String propertyName, Class<?> propertyType) {
        this(table, name, type, propertyName, propertyType, null, null);
    }

    public ResolvedColumnMetadata(TableMetadata table, String name, Class<?> type, String propertyName, Class<?> propertyType, ValueGenerationType generationType, String valueGenerator) {
        this(table, name, type, propertyName, propertyType, null, generationType, valueGenerator);
    }

    public ResolvedColumnMetadata(TableMetadata table, String name, Class<?> type, String propertyName, Class<?> propertyType, ColumnMetadata foreignReference) {
        this(table, name, type, propertyName, propertyType, foreignReference, null, null);
    }

    public ResolvedColumnMetadata(TableMetadata table, String name, Class<?> type, String propertyName, Class<?> propertyType, ColumnMetadata foreignReference, ValueGenerationType generationType, String valueGenerator) {
        super(name, table);
        this.type = type;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.foreignReference = foreignReference;
        this.generationType = generationType;
        this.valueGenerator = valueGenerator;
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

    @Override
    public ValueGenerationType getGenerationType() {
        return generationType;
    }

    @Override
    public String getValueGenerator() {
        return valueGenerator;
    }

}
