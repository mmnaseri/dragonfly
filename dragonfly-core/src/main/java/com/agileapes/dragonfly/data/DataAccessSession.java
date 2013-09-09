package com.agileapes.dragonfly.data;

import com.agileapes.dragonfly.data.impl.DefaultDataStructureHandler;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/7, 14:26)
 */
public class DataAccessSession {

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

    public DataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry) {
        this(databaseDialect, statementRegistry, metadataRegistry, null, null);
    }

    public DataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry, String hostName, String databaseName) {
        this(databaseDialect, statementRegistry, metadataRegistry, hostName, databaseName, null, null);
    }

    public DataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry, String hostName, String databaseName, String username, String password) {
        this(databaseDialect, statementRegistry, metadataRegistry, hostName, null, databaseName, username, password);
    }

    public DataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry, String hostName, Integer port, String databaseName, String username, String password) {
        this(databaseDialect, statementRegistry, metadataRegistry, new JdbcDataSource(getConnectionString(databaseDialect, hostName, port, databaseName)), username, password);
    }

    public DataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry, String connectionString) {
        this(databaseDialect, statementRegistry, metadataRegistry, connectionString, null, null);
    }

    public DataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry, String connectionString, String username, String password) {
        this(databaseDialect, statementRegistry, metadataRegistry, new JdbcDataSource(connectionString), username, password);
    }

    public DataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry, DataSource dataSource) {
        this(databaseDialect, statementRegistry, metadataRegistry, dataSource, null, null);
    }

    public DataAccessSession(DatabaseDialect databaseDialect, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry, DataSource dataSource, String username, String password) {
        this.databaseDialect = databaseDialect;
        this.statementRegistry = statementRegistry;
        this.metadataRegistry = metadataRegistry;
        this.dataSource = dataSource;
        this.username = username;
        this.password = password;
        this.dataStructureHandler = new DefaultDataStructureHandler(this);
    }

    public Connection getConnection() throws SQLException {
        if (username != null && password != null) {
            return dataSource.getConnection(username, password);
        } else {
            return dataSource.getConnection();
        }
    }

    public DatabaseDialect getDatabaseDialect() {
        return databaseDialect;
    }

    public StatementRegistry getStatementRegistry() {
        return statementRegistry;
    }

    public MetadataRegistry getMetadataRegistry() {
        return metadataRegistry;
    }

    public Collection<Class<?>> getRegisteredEntities() {
        return metadataRegistry.getEntityTypes();
    }

    public void initialize() {
        dataStructureHandler.initialize();
    }

    public boolean isInitialized() {
        return initialized;
    }

}
