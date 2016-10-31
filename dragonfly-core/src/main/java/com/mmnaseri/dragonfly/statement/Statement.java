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

import com.mmnaseri.dragonfly.entity.EntityMapCreator;
import com.mmnaseri.dragonfly.metadata.TableMetadata;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * This interface encapsulates all the behaviour and data expected of a statement throughout the framework's
 * activities.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/3, 17:58)
 */
public interface Statement {

    /**
     * Determines whether or not the statement is dynamic. If a statement is not dynamic, i.e. it is static,
     * the SQL statement available through {@link #getSql()} needs no further processing and can be sent to
     * the underlying data source. However, if the statement is marked as dynamic, it has to be processed to
     * be presentable for final execution.
     * @return {@code true} means that the statement is dynamic.
     */
    boolean isDynamic();

    /**
     * Determines whether the statement requires parameters to be set, or if it is complete without any
     * preparation.
     * @return {@code true} means the statement cannot be prepared without passing some parameters
     */
    boolean hasParameters();

    /**
     * @return the native statement in SQL language for the statement, which might require further processing
     * depending on the values of {@link #isDynamic()} and {@link #hasParameters()}
     */
    String getSql();

    /**
     * @return the type of the statement as understood by the framework
     */
    StatementType getType();

    /**
     * @return the table metadata for the table on which this statement will operate.
     */
    TableMetadata<?> getTableMetadata();

    /**
     * Prepares the statement via the given connection, assuming no parameters or processing is needed
     * @param connection    the connection through which the statement must be prepared
     * @return the prepared statement ready to be executed
     */
    PreparedStatement prepare(Connection connection);

    /**
     * Prepares the statement via the given connection, assuming that the statement needs processing based
     * on the given parameters. If the value of {@link #isDynamic()} is set to {@code true}, then the statement
     * will undergo a second-pass parsing, as well.
     * @param connection    the connection through which the statement must be prepared
     * @param mapCreator    the helper which will produce a map from a given entity object. Can be set to
     *                      {@code null} if the passed value is already a map
     * @param value         the object from which values must be read and passed on to the statement for final
     *                      processing before it is prepared for execution.
     * @return the prepared statement ready to be executed
     */
    PreparedStatement prepare(Connection connection, EntityMapCreator mapCreator, Object value);

    /**
     * Prepares the statement via the given connection, assuming that the statement needs processing based
     * on the given parameters. If the value of {@link #isDynamic()} is set to {@code true}, then the statement
     * will undergo a second-pass parsing, as well.
     * @param connection    the connection through which the statement must be prepared
     * @param mapCreator    the helper which will produce a map from a given entity object. Can be set to
     *                      {@code null} if the passed value is already a map
     * @param value         the object from which values must be read and passed on to the statement for final
     *                      processing before it is prepared for execution.
     * @param replacement   the values which are expected to replace the values represented by {@code value}. This
     *                      is syntactically no different than passing the same arguments through the {@code value}
     *                      parameter, but it arranges for a different semantics, when an update is intended.
     * @return the prepared statement ready to be executed
     */
    PreparedStatement prepare(Connection connection, EntityMapCreator mapCreator, Object value, Object replacement);

}
