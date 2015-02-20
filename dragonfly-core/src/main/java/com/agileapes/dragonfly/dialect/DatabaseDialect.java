/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.agileapes.dragonfly.dialect;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.ValueGenerationType;
import com.agileapes.dragonfly.statement.StatementBuilderContext;

import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * This interface holds all vendor-specific and dialectal information needed throughout the
 * framework for establishing proper relationship with an underlying database system
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/1, 1:33)
 */
public interface DatabaseDialect extends Filter<DatabaseMetaData> {

    /**
     * @return the character that will escape an identifier when wrapping around it
     */
    Character getIdentifierEscapeCharacter();

    /**
     * @return the character that acts as separator between schema and database names
     */
    Character getSchemaSeparator();

    /**
     * @return the character that is used to escape search strings
     */
    Character getStringEscapeCharacter();

    /**
     * This method will return vendor defined types for each corresponding JDBC defined type.
     * JDBC types are defined through {@link java.sql.Types}
     * @param columnMetadata    column metadata for which column type is required
     * @return the SQL statement that is accepted by the vendor to be the equivalent of the
     * defined type
     */
    String getType(ColumnMetadata columnMetadata);

    /**
     * @return the statement builder context used by the dialect to reflect dialectal statements
     */
    StatementBuilderContext getStatementBuilderContext();

    /**
     * Retrieves generated key data for the given table from the given result set
     *
     * @param generatedKeys    the result set containing generated keys
     * @return the generated key
     * @throws SQLException
     */
    Serializable retrieveKey(ResultSet generatedKeys) throws SQLException;

    /**
     * @return the name of the dialect
     */
    String getName();

    /**
     * @return the class name for the underlying driver used by this dialect
     */
    String getDriverClassName();

    /**
     * @return the default port through which connections can be made to the database
     */
    int getDefaultPort();

    /**
     * Determines whether or not a table has been defined with the database
     * @param databaseMetadata    the database metadata for the connection
     * @param tableMetadata       the table metadata
     * @param <E>                 the type of the entity for which the metadata is given
     * @return {@code true} means that the table exists
     */
    <E> boolean hasTable(DatabaseMetaData databaseMetadata, TableMetadata<E> tableMetadata);

    /**
     * @return the name of the column associated with the count of data as specified by
     * the statements defined through the statement builder context for the dialect
     */
    String getCountColumn();

    /**
     * Attempts to load values for all keys generated through sequences based on the
     * given table metadata
     * @param tableMetadata    the table for which retrieval of keys must occur
     * @param <E>              the type of the entity for the key
     * @return a map of values for the retrieved keys
     */
    <E> Map<String,Object> loadSequenceValues(TableMetadata<E> tableMetadata);

    /**
     * Will load keys from table generated values.
     * @param generatorMetadata    the metadata for key-generating table
     * @param tableMetadata        the metadata for the table for which keys will be generated
     * @param session              the session through which connections must be established
     * @param <E>                  the type of the entity for the target
     * @return a map of values for the retrieved keys
     */
    <E> Map<String,Object> loadTableValues(TableMetadata<?> generatorMetadata, TableMetadata<E> tableMetadata, DataAccessSession session);

    ValueGenerationType getDefaultGenerationType();

    QueryPagingDecorator getPagingDecorator();

    boolean isGenerationTypeSupported(ValueGenerationType generationType);

}
