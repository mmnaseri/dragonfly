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

package com.agileapes.dragonfly.data;

import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;

import java.sql.Connection;
import java.util.Collection;

/**
 * This interface exposes functionality required from a single database session. It is usually sufficient
 * to initialize a single session per application, unless it is necessary to have more than one
 * secured, closed domain of data access object interaction.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
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

}
