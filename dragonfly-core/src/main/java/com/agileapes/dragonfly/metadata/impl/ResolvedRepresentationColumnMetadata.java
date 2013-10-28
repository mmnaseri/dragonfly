package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.ValueGenerationType;
import com.agileapes.dragonfly.metadata.impl.ResolvedColumnMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/3, 9:41)
 */
public class ResolvedRepresentationColumnMetadata extends ResolvedColumnMetadata {

    private final String actualProperty;

    public ResolvedRepresentationColumnMetadata(TableMetadata<?> table, Class<?> declaringClass, String name, int type, String propertyName, Class<?> propertyType, boolean nullable, int length, int precision, int scale, ValueGenerationType generationType, String valueGenerator, ColumnMetadata foreignReference, String actualProperty) {
        super(table, declaringClass, name, type, propertyName, propertyType, nullable, length, precision, scale, generationType, valueGenerator, foreignReference);
        this.actualProperty = actualProperty;
    }

    public String getActualProperty() {
        return actualProperty;
    }

}
