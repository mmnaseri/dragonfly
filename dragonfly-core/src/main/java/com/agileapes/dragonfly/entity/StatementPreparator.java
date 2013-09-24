package com.agileapes.dragonfly.entity;

import com.agileapes.dragonfly.metadata.TableMetadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;

/**
 * This interface allows for a given statement to be made into a prepared statement for a given connection,
 * so that it can benefit from the pre-compilation process available through some database drivers.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 17:00)
 */
public interface StatementPreparator {

    /**
     * Prepares the statement for the given connection
     * @param connection       the connection for which the statement will be prepared
     * @param tableMetadata    the table metadata for the statement
     * @param value            the map of values to be interpolated into the statement
     * @param sql              the SQL statement
     * @return the prepared statement
     */
    PreparedStatement prepare(Connection connection, TableMetadata<?> tableMetadata, Map<String, Object> value, String sql);

    /**
     * Prepares the statement for the given connection
     * @param statement        the statement to be prepared
     * @param tableMetadata    the table metadata for the statement
     * @param value            the map of values to be interpolated into the statement
     * @param sql              the SQL statement
     * @return the prepared statement
     */
    PreparedStatement prepare(PreparedStatement statement, TableMetadata<?> tableMetadata, Map<String, Object> value, String sql);

}
