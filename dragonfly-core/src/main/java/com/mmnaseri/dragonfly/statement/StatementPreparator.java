/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
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

package com.mmnaseri.dragonfly.statement;

import com.mmnaseri.dragonfly.metadata.TableMetadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

/**
 * This interface allows for a given statement to be made into a prepared statement for a given connection,
 * so that it can benefit from the pre-compilation process available through some database drivers.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
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
