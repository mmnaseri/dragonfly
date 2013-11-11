/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.metadata;

/**
 * This interface encapsulates column metadata for any given column. Various aspects of the
 * column definition can be deduced from examining the methods of this interface, most of
 * which can be seen as simple getter methods for an underlying (im)mutable object.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:07)
 */
public interface ColumnMetadata extends Metadata {

    /**
     * @return the name of the column
     */
    String getName();

    /**
     * @return the type constant for the column, as defined and recognized through JDBC
     * @see java.sql.Types
     */
    int getType();

    /**
     * @return the name of the property whose value is provided by and corresponds to the
     * given column
     */
    String getPropertyName();

    /**
     * @return the type of the property, as understood by the Java compiler
     */
    Class<?> getPropertyType();

    /**
     * @return the column this column refers to, or {@code null} if this is not a foreign
     * key column
     */
    ColumnMetadata getForeignReference();

    /**
     * @return the table which defines and contains this column
     */
    TableMetadata<?> getTable();

    /**
     * @return the value generation scheme for the column, or {@code null} if values must
     * be provided manually for this column
     */
    ValueGenerationType getGenerationType();

    /**
     * @return the name of the identity generating values for this column, or {@code null}
     * if values must be provided manually for this column
     */
    String getValueGenerator();

    /**
     * @return {@code true} if this column can be set to {@code null}. In other words, {@code true}
     * means that specifying values for this column is optional.
     */
    boolean isNullable();

    /**
     * @return the length of the values for this column, if it applies to the given type.
     */
    int getLength();

    /**
     * @return the precision of the values stored in the database for the column, if applicable
     * to the type
     */
    int getPrecision();

    /**
     * @return the scale of the values stored in the database for the column, if applicable to
     * the type
     */
    int getScale();

    /**
     * @return the class that declares the column. Note that this is not necessarily the same
     * as the class for which the table metadata has been defined, as column metadata can be
     * inherited and/or augmented through extensions.
     */
    Class<?> getDeclaringClass();

}
