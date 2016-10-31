/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
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

package com.agileapes.dragonfly.dialect.impl;

import com.mmnaseri.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.error.UnknownColumnTypeError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.statement.StatementBuilderContext;
import com.agileapes.dragonfly.statement.Statements;
import com.agileapes.dragonfly.statement.impl.DefaultStatementBuilderContext;
import com.agileapes.dragonfly.statement.impl.FreemarkerStatementBuilder;
import freemarker.template.Configuration;

import java.sql.Types;
import java.util.Collections;
import java.util.Map;

/**
 * This database dialect will prepare most of what a dialect should have, so that extending
 * dialects only need to specify vendor-specific details.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 1:24)
 */
public abstract class GenericDatabaseDialect implements DatabaseDialect {

    private final DefaultStatementBuilderContext statementBuilderContext;

    public GenericDatabaseDialect() {
        final Configuration configuration = FreemarkerUtils.getConfiguration(getClass(), "/sql/standard");
        statementBuilderContext = new DefaultStatementBuilderContext();
        statementBuilderContext.register(Statements.Definition.CREATE_FOREIGN_KEY, new FreemarkerStatementBuilder(configuration, "createForeignKey.sql.ftl", this));
        statementBuilderContext.register(Statements.Definition.CREATE_PRIMARY_KEY, new FreemarkerStatementBuilder(configuration, "createPrimaryKey.sql.ftl", this));
        statementBuilderContext.register(Statements.Definition.CREATE_UNIQUE_CONSTRAINT, new FreemarkerStatementBuilder(configuration, "createUniqueConstraint.sql.ftl", this));
        statementBuilderContext.register(Statements.Definition.CREATE_TABLE, new FreemarkerStatementBuilder(configuration, "createTable.sql.ftl", this));
        statementBuilderContext.register(Statements.Definition.DROP_FOREIGN_KEY, new FreemarkerStatementBuilder(configuration, "dropForeignKey.sql.ftl", this));
        statementBuilderContext.register(Statements.Definition.DROP_PRIMARY_KEY, new FreemarkerStatementBuilder(configuration, "dropPrimaryKey.sql.ftl", this));
        statementBuilderContext.register(Statements.Definition.DROP_TABLE, new FreemarkerStatementBuilder(configuration, "dropTable.sql.ftl", this));
        statementBuilderContext.register(Statements.Definition.DROP_UNIQUE_CONSTRAINT, new FreemarkerStatementBuilder(configuration, "dropUniqueConstraint.sql.ftl", this));
        statementBuilderContext.register(Statements.Manipulation.DELETE_ALL, new FreemarkerStatementBuilder(configuration, "deleteAll.sql.ftl", this));
        statementBuilderContext.register(Statements.Manipulation.DELETE_ONE, new FreemarkerStatementBuilder(configuration, "deleteByKey.sql.ftl", this));
        statementBuilderContext.register(Statements.Manipulation.DELETE_LIKE, new FreemarkerStatementBuilder(configuration, "deleteBySample.sql.ftl", this));
        statementBuilderContext.register(Statements.Manipulation.DELETE_DEPENDENCIES, new FreemarkerStatementBuilder(configuration, "deleteDependencies.sql.ftl", this));
        statementBuilderContext.register(Statements.Manipulation.DELETE_DEPENDENTS, new FreemarkerStatementBuilder(configuration, "deleteDependents.sql.ftl", this));
        statementBuilderContext.register(Statements.Manipulation.FIND_ONE, new FreemarkerStatementBuilder(configuration, "findByKey.sql.ftl", this));
        statementBuilderContext.register(Statements.Manipulation.COUNT_ALL, new FreemarkerStatementBuilder(configuration, "countAll.sql.ftl", this));
        statementBuilderContext.register(Statements.Manipulation.COUNT_ONE, new FreemarkerStatementBuilder(configuration, "countByKey.sql.ftl", this));
        statementBuilderContext.register(Statements.Manipulation.COUNT_LIKE, new FreemarkerStatementBuilder(configuration, "countBySample.sql.ftl", this));
        statementBuilderContext.register(Statements.Manipulation.INSERT, new FreemarkerStatementBuilder(configuration, "insert.sql.ftl", this));
        statementBuilderContext.register(Statements.Manipulation.UPDATE, new FreemarkerStatementBuilder(configuration, "updateBySample.sql.ftl", this));
        statementBuilderContext.register(Statements.Manipulation.TRUNCATE, new FreemarkerStatementBuilder(configuration, "truncate.sql.ftl", this));
        statementBuilderContext.register(Statements.Manipulation.CALL, new FreemarkerStatementBuilder(configuration, "callProcedure.sql.ftl", this));
        statementBuilderContext.register(Statements.Manipulation.LOAD_MANY_TO_MANY, new FreemarkerStatementBuilder(configuration, "loadManyToMany.sql.ftl", this));
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
            return "NUMERIC (" + (columnMetadata.getPrecision() <= 0 ? 1 : columnMetadata.getPrecision()) +
                    (columnMetadata.getScale() <= 0 ? "" : "," + columnMetadata.getScale()) + ")";
        } else if (columnType == Types.DECIMAL) {
            return "DECIMAL (" + (columnMetadata.getPrecision() <= 0 ? 1 : columnMetadata.getPrecision()) +
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

    @Override
    public <E> Map<String, Object> loadSequenceValues(TableMetadata<E> tableMetadata) {
        return Collections.emptyMap();
    }

}
