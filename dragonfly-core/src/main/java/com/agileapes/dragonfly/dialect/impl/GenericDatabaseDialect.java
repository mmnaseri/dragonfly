package com.agileapes.dragonfly.dialect.impl;

import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.error.UnknownColumnTypeError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.statement.StatementBuilderContext;
import com.agileapes.dragonfly.statement.Statements;
import com.agileapes.dragonfly.statement.impl.FreemarkerStatementBuilder;
import com.agileapes.dragonfly.statement.impl.FreemarkerStatementBuilderContext;
import freemarker.template.Configuration;

import java.sql.Types;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 1:24)
 */
public abstract class GenericDatabaseDialect implements DatabaseDialect {

    private final FreemarkerStatementBuilderContext statementBuilderContext;

    public GenericDatabaseDialect() {
        final Configuration configuration = FreemarkerUtils.getConfiguration(getClass(), "/sql/standard");
        statementBuilderContext = new FreemarkerStatementBuilderContext();
        statementBuilderContext.register(Statements.Definition.CREATE_FOREIGN_KEY, new FreemarkerStatementBuilder(configuration, "createForeignKey.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Definition.CREATE_PRIMARY_KEY, new FreemarkerStatementBuilder(configuration, "createPrimaryKey.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Definition.CREATE_UNIQUE_CONSTRAINT, new FreemarkerStatementBuilder(configuration, "createUniqueConstraint.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Definition.CREATE_TABLE, new FreemarkerStatementBuilder(configuration, "createTable.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Definition.DROP_FOREIGN_KEY, new FreemarkerStatementBuilder(configuration, "dropForeignKey.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Definition.DROP_PRIMARY_KEY, new FreemarkerStatementBuilder(configuration, "dropPrimaryKey.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Definition.DROP_TABLE, new FreemarkerStatementBuilder(configuration, "dropTable.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Definition.DROP_UNIQUE_CONSTRAINT, new FreemarkerStatementBuilder(configuration, "dropUniqueConstraint.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.DELETE_ALL, new FreemarkerStatementBuilder(configuration, "deleteAll.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.DELETE_ONE, new FreemarkerStatementBuilder(configuration, "deleteByKey.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.DELETE_LIKE, new FreemarkerStatementBuilder(configuration, "deleteBySample.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.DELETE_DEPENDENCIES, new FreemarkerStatementBuilder(configuration, "deleteDependencies.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.DELETE_DEPENDENTS, new FreemarkerStatementBuilder(configuration, "deleteDependents.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.FIND_ALL, new FreemarkerStatementBuilder(configuration, "findAll.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.FIND_ONE, new FreemarkerStatementBuilder(configuration, "findByKey.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.FIND_LIKE, new FreemarkerStatementBuilder(configuration, "findBySample.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.COUNT_ALL, new FreemarkerStatementBuilder(configuration, "countAll.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.COUNT_ONE, new FreemarkerStatementBuilder(configuration, "countByKey.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.COUNT_LIKE, new FreemarkerStatementBuilder(configuration, "countBySample.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.INSERT, new FreemarkerStatementBuilder(configuration, "insert.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.UPDATE, new FreemarkerStatementBuilder(configuration, "updateBySample.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.TRUNCATE, new FreemarkerStatementBuilder(configuration, "truncate.sql.ftl", getDatabaseDialect()));
        statementBuilderContext.register(Statements.Manipulation.CALL, new FreemarkerStatementBuilder(configuration, "callProcedure.sql.ftl", getDatabaseDialect()));
    }

    protected DatabaseDialect getDatabaseDialect() {
        return this;
    }

    @Override
    public Character getIdentifierEscapeCharacter() {
        return '"';
    }

    @Override
    public Character getSchemaSeparator() {
        return '.';
    }

    @Override
    public Character getStringEscapeCharacter() {
        return '\\';
    }

    @Override
    public String getType(ColumnMetadata columnMetadata) {
        final int columnType = columnMetadata.getType();
        if (columnType == Types.BIT) {
            return "BIT (" + (columnMetadata.getLength() <= 0 ? 1 : columnMetadata.getLength()) + ")";
        } else if (columnType == Types.TINYINT) {
            return "TINYINT";
        } else if (columnType == Types.SMALLINT) {
            return "SMALLINT";
        } else if (columnType == Types.INTEGER) {
            return "INTEGER";
        } else if (columnType == Types.BIGINT) {
            return "BIGINT";
        } else if (columnType == Types.FLOAT) {
            return "FLOAT";
        } else if (columnType == Types.REAL) {
            return "REAL";
        } else if (columnType == Types.DOUBLE) {
            return "DOUBLE";
        } else if (columnType == Types.NUMERIC) {
            return "NUMERIC (" + (columnMetadata.getPrecision() <= 0 ? "M" : columnMetadata.getPrecision()) +
                    (columnMetadata.getScale() <= 0 ? "" : "," + columnMetadata.getScale()) + ")";
        } else if (columnType == Types.DECIMAL) {
            return "DECIMAL (" + (columnMetadata.getPrecision() <= 0 ? "M" : columnMetadata.getPrecision()) +
                    (columnMetadata.getScale() <= 0 ? "" : "," + columnMetadata.getScale()) + ")";
        } else if (columnType == Types.CHAR) {
            return "CHAR (" + (columnMetadata.getLength() <= 0 ? 1 : (columnMetadata.getLength() > 255 ? 255 : columnMetadata.getLength())) + ")";
        } else if (columnType == Types.VARCHAR) {
            return "VARCHAR (" + (columnMetadata.getLength() <= 0 ? 1 : (columnMetadata.getLength() > 65535 ? 65535 : columnMetadata.getLength())) + ")";
        } else if (columnType == Types.LONGVARCHAR) {
            return "LONG VARCHAR";
        } else if (columnType == Types.DATE) {
            return "DATE";
        } else if (columnType == Types.TIME) {
            return "TIME";
        } else if (columnType == Types.TIMESTAMP) {
            return "DATETIME";
        } else if (columnType == Types.BINARY) {
            return "BINARY (" + (columnMetadata.getLength() <= 0 ? 1 : (columnMetadata.getLength() > 255 ? 255 : columnMetadata.getLength())) + ")";
        } else if (columnType == Types.VARBINARY) {
            return "VARBINARY (" + (columnMetadata.getLength() <= 0 ? 1 : (columnMetadata.getLength() > 65535 ? 65535 : columnMetadata.getLength())) + ")";
        } else if (columnType == Types.LONGVARBINARY) {
            return "LONGVARBINARY";
        } else if (columnType == Types.BOOLEAN) {
            return "BOOLEAN";
        }
        throw new UnknownColumnTypeError(columnType);
    }

    @Override
    public StatementBuilderContext getStatementBuilderContext() {
        return statementBuilderContext;
    }

    @Override
    public String getCountColumn() {
        return "cnt";
    }

}
