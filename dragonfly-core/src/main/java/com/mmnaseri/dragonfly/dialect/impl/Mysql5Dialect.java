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

package com.mmnaseri.dragonfly.dialect.impl;

import com.mmnaseri.couteau.basics.api.Filter;
import com.mmnaseri.couteau.basics.api.Processor;
import com.mmnaseri.couteau.freemarker.utils.FreemarkerUtils;
import com.mmnaseri.dragonfly.data.DataAccessSession;
import com.mmnaseri.dragonfly.dialect.DatabaseDialect;
import com.mmnaseri.dragonfly.dialect.QueryPagingDecorator;
import com.mmnaseri.dragonfly.entity.RowHandler;
import com.mmnaseri.dragonfly.entity.impl.DefaultRowHandler;
import com.mmnaseri.dragonfly.error.DatabaseMetadataAccessError;
import com.mmnaseri.dragonfly.error.MetadataCollectionError;
import com.mmnaseri.dragonfly.error.UnsuccessfulOperationError;
import com.mmnaseri.dragonfly.metadata.ColumnMetadata;
import com.mmnaseri.dragonfly.metadata.TableMetadata;
import com.mmnaseri.dragonfly.metadata.ValueGenerationType;
import com.mmnaseri.dragonfly.statement.StatementBuilderContext;
import com.mmnaseri.dragonfly.statement.Statements;
import com.mmnaseri.dragonfly.statement.impl.DefaultStatementBuilderContext;
import com.mmnaseri.dragonfly.statement.impl.FreemarkerStatementBuilder;
import com.mmnaseri.dragonfly.tools.DatabaseUtils;
import freemarker.template.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * Adds vendor-specific details for MySQL 5
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/4, 14:15)
 */
public class Mysql5Dialect extends GenericDatabaseDialect {

    private static final Log log = LogFactory.getLog(DatabaseDialect.class);
    private final RowHandler rowHandler;
    private final QueryPagingDecorator pagingDecorator;

    public Mysql5Dialect() {
        StatementBuilderContext statementBuilderContext = getStatementBuilderContext();
        final Configuration configuration = FreemarkerUtils.getConfiguration(getClass(), "/sql/mysql5");
        ((DefaultStatementBuilderContext) statementBuilderContext).register(Statements.Definition.BIND_SEQUENCE, new FreemarkerStatementBuilder(configuration, "bindSequence.sql.ftl", this));
        ((DefaultStatementBuilderContext) statementBuilderContext).register(Statements.Definition.UNBIND_SEQUENCE, new FreemarkerStatementBuilder(configuration, "unbindSequence.sql.ftl", this));
        ((DefaultStatementBuilderContext) statementBuilderContext).register(Statements.Definition.DROP_FOREIGN_KEY, new FreemarkerStatementBuilder(configuration, "dropForeignKey.sql.ftl", this));
        ((DefaultStatementBuilderContext) statementBuilderContext).register(Statements.Definition.DROP_PRIMARY_KEY, new FreemarkerStatementBuilder(configuration, "dropPrimaryKey.sql.ftl", this));
        ((DefaultStatementBuilderContext) statementBuilderContext).register(Statements.Definition.CREATE_TABLE, new FreemarkerStatementBuilder(configuration, "createTable.sql.ftl", this));
        ((DefaultStatementBuilderContext) statementBuilderContext).register(Statements.Manipulation.FIND_LIKE, new FreemarkerStatementBuilder(configuration, "findBySample.sql.ftl", this));
        ((DefaultStatementBuilderContext) statementBuilderContext).register(Statements.Manipulation.FIND_ALL, new FreemarkerStatementBuilder(configuration, "findAll.sql.ftl", this));
        this.rowHandler = new DefaultRowHandler();
        log.info("Initializing database dialect " + getClass().getSimpleName() + " for " + getName());
        pagingDecorator = new QueryPagingDecorator() {
            @Override
            public String decorate(String query, int pageSize, int pageNumber) {
                query = query.replaceAll(";\\s*$", ""); //trim the ending semi-colon, if any exists
                query += " LIMIT " + ((pageNumber - 1) * pageSize) + ", " + pageSize + ";";
                return query;
            }
        };
    }

    @Override
    public Character getIdentifierEscapeCharacter() {
        return '`';
    }

    @Override
    public boolean accepts(DatabaseMetaData databaseMetaData) {
        try {
            return databaseMetaData.getDatabaseProductName().toLowerCase().matches("mysql") && databaseMetaData.getDatabaseMajorVersion() == 5;
        } catch (SQLException e) {
            throw new DatabaseMetadataAccessError(e);
        }
    }

    @Override
    public Serializable retrieveKey(ResultSet generatedKeys) throws SQLException{
        return generatedKeys.getLong(1);
    }

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public int getDefaultPort() {
        return 3306;
    }

    @Override
    public <E> boolean hasTable(DatabaseMetaData databaseMetadata, TableMetadata<E> tableMetadata) {
        try {
            String schema = tableMetadata.getSchema() != null && !tableMetadata.getSchema().isEmpty() ? tableMetadata.getSchema() : databaseMetadata.getConnection().getCatalog();
            return databaseMetadata.getTables(schema, null, tableMetadata.getName(), new String[]{"TABLE"}).next();
        } catch (SQLException e) {
            throw new MetadataCollectionError("Failed to recognize database metadata", e);
        }
    }

    @Override
    public synchronized <E> Map<String, Object> loadTableValues(final TableMetadata<?> generatorMetadata, TableMetadata<E> tableMetadata, final DataAccessSession session) {
        final HashMap<String, Object> result = new HashMap<String, Object>();
        with(tableMetadata.getColumns())
                .forThose(
                        new Filter<ColumnMetadata>() {
                            @Override
                            public boolean accepts(ColumnMetadata item) {
                                return ValueGenerationType.TABLE.equals(item.getGenerationType());
                            }
                        },
                        new Processor<ColumnMetadata>() {
                            @Override
                            public void process(ColumnMetadata column) {
                                final String valueGenerator;
                                if (column.getValueGenerator() == null || column.getValueGenerator().isEmpty()) {
                                    valueGenerator = column.getTable().getEntityType().getCanonicalName() + "." + column.getPropertyName();
                                } else {
                                    valueGenerator = column.getValueGenerator();
                                }
                                initializeGenerator(session, generatorMetadata, valueGenerator);
                                final Connection connection = session.getConnection();
                                try {
                                    connection.setAutoCommit(false);
                                    final Statement statement = connection.createStatement();
                                    final String escapedGenerator = DatabaseUtils.escapeString(valueGenerator, session.getDatabaseDialect().getStringEscapeCharacter());
                                    final String table = DatabaseUtils.qualifyTable(generatorMetadata, session.getDatabaseDialect());
                                    final String query = "SELECT `value` FROM " + table + " WHERE `name` = \"" + escapedGenerator + "\" FOR UPDATE;";
                                    log.trace("Querying for key: " + query);
                                    final ResultSet resultSet = statement.executeQuery(query);
                                    resultSet.next();
                                    final Map<String, Object> map = rowHandler.handleRow(resultSet);
                                    resultSet.close();
                                    final String update = "UPDATE " + table + " SET `value` = `value` + 1 WHERE `name` = \"" + escapedGenerator + "\"";
                                    log.trace("Updating key: " + update);
                                    statement.executeUpdate(update);
                                    result.put(column.getName(), map.get("value"));
                                    connection.commit();
                                    connection.close();
                                } catch (Exception e) {
                                    throw new UnsuccessfulOperationError("Failed to load generated key for " + column.getName(), e);
                                }
                            }
                        }
                );
        return result;
    }

    @Override
    public ValueGenerationType getDefaultGenerationType() {
        return ValueGenerationType.IDENTITY;
    }

    @Override
    public QueryPagingDecorator getPagingDecorator() {
        return pagingDecorator;
    }

    @Override
    public boolean isGenerationTypeSupported(ValueGenerationType generationType) {
        return ValueGenerationType.SEQUENCE.equals(generationType);
    }

    @Override
    public String getDriverClassName() {
        return "com.mysql.jdbc.Driver";
    }

    private synchronized void initializeGenerator(DataAccessSession session, TableMetadata<?> generatorTableMetadata, String valueGenerator) {
        final Connection connection = session.getConnection();
        try {
            connection.setAutoCommit(false);
            final Statement statement = connection.createStatement();
            final String update = "INSERT IGNORE INTO " + DatabaseUtils.qualifyTable(generatorTableMetadata, session.getDatabaseDialect()) + " (`name`, `value`) VALUES(\"" + DatabaseUtils.escapeString(valueGenerator, session.getDatabaseDialect().getStringEscapeCharacter()) + "\", 0);";
            log.trace("Initializing generator: " + update);
            statement.executeUpdate(update);
            statement.close();
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to initialize generator: " + valueGenerator, e);
        }
    }

    @Override
    public String getType(ColumnMetadata columnMetadata) {
        if (columnMetadata.getType() == Types.BOOLEAN) {
            return "BIT";
        }
        return super.getType(columnMetadata);
    }
}
