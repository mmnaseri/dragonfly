package com.agileapes.dragonfly.data;

import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.dialect.impl.Mysql5Dialect;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/7, 14:26)
 */
public class DataAccessSession {

    private static final String JDBC_PREFIX = "jdbc:";
    private static final String PROTOCOL_SPECIFIER = "://";
    private static final String PORT_SEPARATOR = ":";
    private static final String DB_SEPARATOR = "/";
    private static DatabaseDialect defaultDatabaseDialect;
    private static final String DEFAULT_HOST = "localhost";
    private DatabaseDialect databaseDialect;
    private final String username;
    private final String password;
    private final DataSource dataSource;

    private static DatabaseDialect getDefaultDatabaseDialect() {
        if (defaultDatabaseDialect == null) {
            defaultDatabaseDialect = new Mysql5Dialect();
        }
        return defaultDatabaseDialect;
    }

    private static String getConnectionString(DatabaseDialect databaseDialect, String hostName, Integer port, String databaseName) {
        return JDBC_PREFIX + databaseDialect.getName() + PROTOCOL_SPECIFIER + (hostName == null ? DEFAULT_HOST : hostName) + PORT_SEPARATOR + (port == null ? databaseDialect.getDefaultPort() : port) + DB_SEPARATOR + (databaseName == null ? "" : databaseName);
    }

    public DataAccessSession() {
        this(getDefaultDatabaseDialect());
    }

    public DataAccessSession(DatabaseDialect databaseDialect) {
        this(databaseDialect, null, null);
    }

    public DataAccessSession(DatabaseDialect databaseDialect, String hostName, String databaseName) {
        this(databaseDialect, hostName, databaseName, null, null);
    }

    public DataAccessSession(DatabaseDialect databaseDialect, String hostName, String databaseName, String username, String password) {
        this(databaseDialect, hostName, null, databaseName, username, password);
    }

    public DataAccessSession(DatabaseDialect databaseDialect, String hostName, Integer port, String databaseName, String username, String password) {
        this(databaseDialect, new JdbcDataSource(getConnectionString(databaseDialect, hostName, port, databaseName)), username, password);
    }

    public DataAccessSession(DatabaseDialect databaseDialect, String connectionString) {
        this(databaseDialect, connectionString, null, null);
    }

    public DataAccessSession(DatabaseDialect databaseDialect, String connectionString, String username, String password) {
        this(databaseDialect, new JdbcDataSource(connectionString), username, password);
    }

    public DataAccessSession(DatabaseDialect databaseDialect, DataSource dataSource) {
        this(databaseDialect, dataSource, null, null);
    }

    public DataAccessSession(DatabaseDialect databaseDialect, DataSource dataSource, String username, String password) {
        this.databaseDialect = databaseDialect;
        this.dataSource = dataSource;
        this.username = username;
        this.password = password;
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
}
