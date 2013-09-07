package com.agileapes.dragonfly.dialect.impl;

import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.error.DatabaseMetadataAccessError;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.statement.StatementBuilderContext;
import com.agileapes.dragonfly.statement.Statements;
import com.agileapes.dragonfly.statement.impl.FreemarkerStatementBuilder;
import com.agileapes.dragonfly.statement.impl.FreemarkerStatementBuilderContext;
import freemarker.template.Configuration;

import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/4, 14:15)
 */
public class Mysql5Dialect extends GenericDatabaseDialect {

    private final StatementBuilderContext statementBuilderContext;

    public Mysql5Dialect() {
        statementBuilderContext = super.getStatementBuilderContext();
        final Configuration configuration = FreemarkerUtils.getConfiguration(getClass(), "/sql/mysql5");
        ((FreemarkerStatementBuilderContext) statementBuilderContext).register(Statements.Definition.CREATE_SEQUENCE, new FreemarkerStatementBuilder(configuration, "createSequence.sql.ftl", getDatabaseDialect()));
        ((FreemarkerStatementBuilderContext) statementBuilderContext).register(Statements.Definition.DROP_FOREIGN_KEY, new FreemarkerStatementBuilder(configuration, "dropForeignKey.sql.ftl", getDatabaseDialect()));
        ((FreemarkerStatementBuilderContext) statementBuilderContext).register(Statements.Definition.DROP_PRIMARY_KEY, new FreemarkerStatementBuilder(configuration, "dropPrimaryKey.sql.ftl", getDatabaseDialect()));
        ((FreemarkerStatementBuilderContext) statementBuilderContext).register(Statements.Definition.DROP_SEQUENCE, new FreemarkerStatementBuilder(configuration, "dropSequence.sql.ftl", getDatabaseDialect()));
    }

    @Override
    protected DatabaseDialect getDatabaseDialect() {
        return this;
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
    public StatementBuilderContext getStatementBuilderContext() {
        return statementBuilderContext;
    }

    @Override
    public Serializable retrieveKey(ResultSet generatedKeys, TableMetadata<?> tableMetadata) throws SQLException{
        if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
        }
        return null;
    }

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public int getDefaultPort() {
        return 3306;
    }

}
