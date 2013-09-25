package com.agileapes.dragonfly.dialect;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.statement.StatementBuilderContext;

import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

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

}
