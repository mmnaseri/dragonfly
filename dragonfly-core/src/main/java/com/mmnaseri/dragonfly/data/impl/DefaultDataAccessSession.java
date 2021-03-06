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

package com.mmnaseri.dragonfly.data.impl;

import com.mmnaseri.couteau.basics.api.Processor;
import com.mmnaseri.dragonfly.data.DataAccessSession;
import com.mmnaseri.dragonfly.data.DataStructureHandler;
import com.mmnaseri.dragonfly.dialect.DatabaseDialect;
import com.mmnaseri.dragonfly.error.DataAccessSessionAlreadyInitializedError;
import com.mmnaseri.dragonfly.error.DataAccessSessionInitializationError;
import com.mmnaseri.dragonfly.error.DatabaseConnectionError;
import com.mmnaseri.dragonfly.error.DatabaseDriverNotFoundError;
import com.mmnaseri.dragonfly.metadata.TableMetadataRegistry;
import com.mmnaseri.dragonfly.statement.StatementRegistry;
import com.mmnaseri.dragonfly.statement.impl.LocalStatementRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class encapsulates a session of interaction with the database. It is usually sufficient
 * to initialize a single session per application, unless it is necessary to have more than one
 * secured, closed domain of data access object interaction.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/7, 14:26)
 */
public class DefaultDataAccessSession implements DataAccessSession {

    private static final Log log = LogFactory.getLog(DefaultDataAccessSession.class);
    private static final String JDBC_PREFIX = "jdbc:";
    private static final String PROTOCOL_SPECIFIER = "://";
    private static final String PORT_SEPARATOR = ":";
    private static final String DB_SEPARATOR = "/";
    private static final String DEFAULT_HOST = "localhost";
    public static final long DEFAULT_CONNECTION_THRESHOLD = 100L;
    public static final long DEFAULT_WAIT_LENIENCY = 1000L;
    private DatabaseDialect databaseDialect;
    private final String username;
    private final String password;
    private final DataSource dataSource;
    private final StatementRegistry statementRegistry;
    private final TableMetadataRegistry tableMetadataRegistry;
    private final DataStructureHandler dataStructureHandler;
    private boolean initialized = false;
    private final AtomicLong connections = new AtomicLong(0L);
    private long maxConnections = DEFAULT_CONNECTION_THRESHOLD;
    private long waitLeniency = DEFAULT_WAIT_LENIENCY;
    private final ThreadLocal<DelegatingConnection> localConnection;

    private static String getConnectionString(DatabaseDialect databaseDialect, String hostName, Integer port, String databaseName) {
        return JDBC_PREFIX + databaseDialect.getName() + PROTOCOL_SPECIFIER + (hostName == null ? DEFAULT_HOST : hostName) + PORT_SEPARATOR + (port == null ? databaseDialect.getDefaultPort() : port) + DB_SEPARATOR + (databaseName == null ? "" : databaseName);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, TableMetadataRegistry tableMetadataRegistry) {
        this(databaseDialect, statementRegistry, tableMetadataRegistry, null, null);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, TableMetadataRegistry tableMetadataRegistry, String hostName, String databaseName) {
        this(databaseDialect, statementRegistry, tableMetadataRegistry, hostName, databaseName, null, null);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, TableMetadataRegistry tableMetadataRegistry, String hostName, String databaseName, String username, String password) {
        this(databaseDialect, statementRegistry, tableMetadataRegistry, hostName, null, databaseName, username, password);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, TableMetadataRegistry tableMetadataRegistry, String hostName, Integer port, String databaseName, String username, String password) {
        this(databaseDialect, statementRegistry, tableMetadataRegistry, new JdbcDataSource(getConnectionString(databaseDialect, hostName, port, databaseName)), username, password);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, TableMetadataRegistry tableMetadataRegistry, String connectionString) {
        this(databaseDialect, statementRegistry, tableMetadataRegistry, connectionString, null, null);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, TableMetadataRegistry tableMetadataRegistry, String connectionString, String username, String password) {
        this(databaseDialect, statementRegistry, tableMetadataRegistry, new JdbcDataSource(connectionString), username, password);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, TableMetadataRegistry tableMetadataRegistry, DataSource dataSource) {
        this(databaseDialect, statementRegistry, tableMetadataRegistry, dataSource, null, null);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, TableMetadataRegistry tableMetadataRegistry, DataSource dataSource, String username, String password) {
        loadDriver(databaseDialect);
        this.databaseDialect = databaseDialect;
        this.statementRegistry = statementRegistry;
        this.tableMetadataRegistry = tableMetadataRegistry;
        this.dataSource = dataSource;
        this.username = username;
        this.password = password;
        this.dataStructureHandler = new DefaultDataStructureHandler(this, tableMetadataRegistry);
        this.localConnection = new ThreadLocal<DelegatingConnection>();
    }

    private void loadDriver(DatabaseDialect databaseDialect) {
        final String className = databaseDialect.getDriverClassName();
        try {
            Class.forName(className, true, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new DatabaseDriverNotFoundError(className);
        }
    }

    /**
     * Returns a connection from the underlying data source, using authentication credentials
     * if necessary
     * @return the connection instance
     */
    @Override
    public synchronized Connection getConnection() {
        if (localConnection.get() != null) {
            final DelegatingConnection connection = localConnection.get();
            connection.open();
            return connection;
        }
        connections.incrementAndGet();
        log.info("[" + connections.get() + "] Connection requested ...");
        long wait = 0;
        while (connections.get() >= maxConnections && wait < waitLeniency) {
            try {
                wait += 100;
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new IllegalStateException("Failed to initialize connection", e);
            }
        }
        if (connections.get() >= maxConnections) {
            log.warn("Threshold exceeded but operations are stacked up, so we will proceed anyway.");
        }
        final Connection connection;
        try {
            if (username != null && password != null && !username.isEmpty()) {
                connection = dataSource.getConnection(username, password);
            } else {
                connection = dataSource.getConnection();
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionError(e);
        }
        final DelegatingConnection delegatingConnection = new DelegatingConnection(connection, new Processor<Connection>() {
            @Override
            public void process(Connection input) {
                log.info("[" + connections.get() + "] Closing connection ...");
                connections.decrementAndGet();
                localConnection.remove();
            }
        });
        delegatingConnection.open();
        localConnection.set(delegatingConnection);
        return delegatingConnection;
    }

    /**
     * @return the dialect associated with the session
     */
    @Override
    public DatabaseDialect getDatabaseDialect() {
        return databaseDialect;
    }

    /**
     * @return the statement registry associated with session
     */
    public StatementRegistry getStatementRegistry(String region) {
        return new LocalStatementRegistry(statementRegistry, region);
    }

    /**
     * @return the statement registry associated with session
     */
    @Override
    public StatementRegistry getStatementRegistry(Class<?> entityType) {
        return getStatementRegistry(entityType.getCanonicalName());
    }

    /**
     * @return the metadata registry associated with the session
     */
    @Override
    public TableMetadataRegistry getTableMetadataRegistry() {
        return tableMetadataRegistry;
    }

    /**
     * @return all entity types for which metadata is registered
     */
    @Override
    public Collection<Class<?>> getRegisteredEntities() {
        return tableMetadataRegistry.getEntityTypes();
    }

    /**
     * Initializes the session by initializing all data structures
     */
    @Override
    public synchronized void initialize() {
        if (initialized) {
            throw new DataAccessSessionAlreadyInitializedError();
        }
        log.info("Initializing data structures with database handlers");
        try {
            dataStructureHandler.initialize();
        } catch (Error e) {
            throw new DataAccessSessionInitializationError("An error prevented successful initialization of the session", e);
        }
        initialized = true;
    }

    /**
     * @return {@code true} if the session has already been initialized
     */
    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void markInitialized() {
        initialized = true;
    }

    public void setMaxConnections(long maxConnections) {
        this.maxConnections = maxConnections;
    }

    public void setWaitLeniency(long waitLeniency) {
        this.waitLeniency = waitLeniency;
    }

    public DataStructureHandler getDataStructureHandler() {
        return dataStructureHandler;
    }

}
