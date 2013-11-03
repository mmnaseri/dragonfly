package com.agileapes.dragonfly.dialect.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.entity.RowHandler;
import com.agileapes.dragonfly.entity.impl.DefaultRowHandler;
import com.agileapes.dragonfly.error.DatabaseMetadataAccessError;
import com.agileapes.dragonfly.error.MetadataCollectionError;
import com.agileapes.dragonfly.error.UnsuccessfulOperationError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.ValueGenerationType;
import com.agileapes.dragonfly.statement.StatementBuilderContext;
import com.agileapes.dragonfly.statement.Statements;
import com.agileapes.dragonfly.statement.impl.FreemarkerStatementBuilder;
import com.agileapes.dragonfly.statement.impl.FreemarkerStatementBuilderContext;
import com.agileapes.dragonfly.tools.DatabaseUtils;
import freemarker.template.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/4, 14:15)
 */
public class Mysql5Dialect extends GenericDatabaseDialect {

    private static final Log log = LogFactory.getLog(DatabaseDialect.class);
    private RowHandler rowHandler;

    public Mysql5Dialect() {
        StatementBuilderContext statementBuilderContext = getStatementBuilderContext();
        final Configuration configuration = FreemarkerUtils.getConfiguration(getClass(), "/sql/mysql5");
        ((FreemarkerStatementBuilderContext) statementBuilderContext).register(Statements.Definition.BIND_SEQUENCE, new FreemarkerStatementBuilder(configuration, "bindSequence.sql.ftl", this));
        ((FreemarkerStatementBuilderContext) statementBuilderContext).register(Statements.Definition.UNBIND_SEQUENCE, new FreemarkerStatementBuilder(configuration, "unbindSequence.sql.ftl", this));
        ((FreemarkerStatementBuilderContext) statementBuilderContext).register(Statements.Definition.DROP_FOREIGN_KEY, new FreemarkerStatementBuilder(configuration, "dropForeignKey.sql.ftl", this));
        ((FreemarkerStatementBuilderContext) statementBuilderContext).register(Statements.Definition.DROP_PRIMARY_KEY, new FreemarkerStatementBuilder(configuration, "dropPrimaryKey.sql.ftl", this));
        ((FreemarkerStatementBuilderContext) statementBuilderContext).register(Statements.Definition.CREATE_TABLE, new FreemarkerStatementBuilder(configuration, "createTable.sql.ftl", this));
        this.rowHandler = new DefaultRowHandler();
        log.info("Initializing database dialect " + getClass().getSimpleName() + " for " + getName());
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

}
