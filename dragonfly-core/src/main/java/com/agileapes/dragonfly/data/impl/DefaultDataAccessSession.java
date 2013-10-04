package com.agileapes.dragonfly.data.impl;

import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.data.DataStructureHandler;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.error.DataAccessSessionInitializationError;
import com.agileapes.dragonfly.error.DatabaseConnectionError;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.statement.impl.LocalStatementRegistry;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * This class encapsulates a session of interaction with the database. It is usually sufficient
 * to initialize a single session per application, unless it is necessary to have more than one
 * secured, closed domain of data access object interaction.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/7, 14:26)
 */
public class DefaultDataAccessSession implements DataAccessSession {

    private static final Log log = LogFactory.getLog(DefaultDataAccessSession.class);
    private static final String JDBC_PREFIX = "jdbc:";
    private static final String PROTOCOL_SPECIFIER = "://";
    private static final String PORT_SEPARATOR = ":";
    private static final String DB_SEPARATOR = "/";
    private static final String DEFAULT_HOST = "localhost";
    private DatabaseDialect databaseDialect;
    private final String username;
    private final String password;
    private final DataSource dataSource;
    private final StatementRegistry statementRegistry;
    private final MetadataRegistry metadataRegistry;
    private final DataStructureHandler dataStructureHandler;
    private boolean initialized = false;

    private static String getConnectionString(DatabaseDialect databaseDialect, String hostName, Integer port, String databaseName) {
        return JDBC_PREFIX + databaseDialect.getName() + PROTOCOL_SPECIFIER + (hostName == null ? DEFAULT_HOST : hostName) + PORT_SEPARATOR + (port == null ? databaseDialect.getDefaultPort() : port) + DB_SEPARATOR + (databaseName == null ? "" : databaseName);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry) {
        this(databaseDialect, statementRegistry, metadataRegistry, null, null);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry, String hostName, String databaseName) {
        this(databaseDialect, statementRegistry, metadataRegistry, hostName, databaseName, null, null);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry, String hostName, String databaseName, String username, String password) {
        this(databaseDialect, statementRegistry, metadataRegistry, hostName, null, databaseName, username, password);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry, String hostName, Integer port, String databaseName, String username, String password) {
        this(databaseDialect, statementRegistry, metadataRegistry, new JdbcDataSource(getConnectionString(databaseDialect, hostName, port, databaseName)), username, password);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry, String connectionString) {
        this(databaseDialect, statementRegistry, metadataRegistry, connectionString, null, null);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry, String connectionString, String username, String password) {
        this(databaseDialect, statementRegistry, metadataRegistry, new JdbcDataSource(connectionString), username, password);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry, DataSource dataSource) {
        this(databaseDialect, statementRegistry, metadataRegistry, dataSource, null, null);
    }

    public DefaultDataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry, DataSource dataSource, String username, String password) {
        this.databaseDialect = databaseDialect;
        this.statementRegistry = statementRegistry;
        this.metadataRegistry = metadataRegistry;
        this.dataSource = dataSource;
        this.username = username;
        this.password = password;
        this.dataStructureHandler = new DefaultDataStructureHandler(this);
    }

    /**
     * Returns a connection from the underlying data source, using authentication credentials
     * if necessary
     * @return the connection instance
     */
    @Override
    public Connection getConnection() {
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
        return new DelegatingConnection(connection);
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
    @Override
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
    public MetadataRegistry getMetadataRegistry() {
        return metadataRegistry;
    }

    /**
     * @return all entity types for which metadata is registered
     */
    @Override
    public Collection<Class<?>> getRegisteredEntities() {
        return metadataRegistry.getEntityTypes();
    }

    /**
     * Initializes the session by initializing all data structures
     */
    @Override
    public synchronized void initialize() {
        if (initialized) {
            throw new DataAccessSessionInitializationError("Session is already initialized");
        }
        log.info("Initializing data structures with database handlers");
        dataStructureHandler.initialize();
        initialized = true;
    }

    /**
     * @return {@code true} if the session has already been initialized
     */
    @Override
    public boolean isInitialized() {
        return initialized;
    }

}
