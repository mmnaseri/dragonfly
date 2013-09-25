package com.agileapes.dragonfly.data;

import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;

import java.sql.Connection;
import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/25, 18:18)
 */
public interface DataAccessSession {

    Connection getConnection();

    DatabaseDialect getDatabaseDialect();

    StatementRegistry getStatementRegistry(String region);

    StatementRegistry getStatementRegistry(Class<?> entityType);

    MetadataRegistry getMetadataRegistry();

    Collection<Class<?>> getRegisteredEntities();

    void initialize();

    boolean isInitialized();

}
