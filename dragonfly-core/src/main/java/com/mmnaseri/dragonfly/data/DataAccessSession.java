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

package com.mmnaseri.dragonfly.data;

import com.mmnaseri.dragonfly.dialect.DatabaseDialect;
import com.mmnaseri.dragonfly.metadata.TableMetadataRegistry;
import com.mmnaseri.dragonfly.statement.StatementRegistry;

import java.sql.Connection;
import java.util.Collection;

/**
 * This interface exposes functionality required from a single database session. It is usually sufficient
 * to initialize a single session per application, unless it is necessary to have more than one
 * secured, closed domain of data access object interaction.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/25, 18:18)
 */
public interface DataAccessSession {

    /**
     * Opens a connection to the data source assigned to the session
     * @return the opened connection. The connection might be a newly opened connection
     * or it might be an already open one.
     */
    Connection getConnection();

    /**
     * @return the database dialect for the current session
     */
    DatabaseDialect getDatabaseDialect();

    /**
     * Returns the statement registry used for the current session. The registry will be
     * secured so that only statements for the given entity type will be accessible.
     * @param entityType    the type of the entity required
     * @return the statement registry for the entity type
     */
    StatementRegistry getStatementRegistry(Class<?> entityType);

    /**
     * @return the metadata registry for all entities registered with the session
     */
    TableMetadataRegistry getTableMetadataRegistry();

    /**
     * @return all registered entity types for the current session
     */
    Collection<Class<?>> getRegisteredEntities();

    /**
     * Initializes the session if not already initialized
     */
    void initialize();

    /**
     * @return {@code true} if the session has been already initialized
     */
    boolean isInitialized();

    /**
     * Marks this session as initialized, or throws an error if it has been already initialized
     */
    void markInitialized();
}
