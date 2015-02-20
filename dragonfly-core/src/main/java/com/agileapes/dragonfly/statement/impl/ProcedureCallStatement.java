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

package com.agileapes.dragonfly.statement.impl;

import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.entity.EntityMapCreator;
import com.agileapes.dragonfly.error.StatementPreparationError;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.statement.Statement;
import com.agileapes.dragonfly.statement.StatementPreparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * This is a statement that represents a procedure call
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 1:38)
 */
public class ProcedureCallStatement extends ImmutableStatement {

    private static final Log log = LogFactory.getLog(Statement.class);
    private final StatementPreparator preparator;

    public ProcedureCallStatement(Statement statement, DatabaseDialect dialect) {
        this(statement.getTableMetadata(), dialect, statement.getSql());
    }

    public ProcedureCallStatement(TableMetadata<?> tableMetadata, DatabaseDialect dialect, String sql) {
        super(tableMetadata, dialect, sql);
        preparator = new DefaultStatementPreparator(true);
    }

    @Override
    public PreparedStatement prepare(Connection connection) {
        try {
            log.info("Preparing statement: " + getSql());
            return connection.prepareCall(getSql());
        } catch (SQLException e) {
            throw new StatementPreparationError("Failed to prepare statement through connection", e);
        }
    }

    @Override
    public CallableStatement prepare(Connection connection, EntityMapCreator mapCreator, Object value) {
        String finalSql = getSql();
        if (isDynamic()) {
            final FreemarkerSecondPassStatementBuilder builder = new FreemarkerSecondPassStatementBuilder(this, getDialect(), value);
            finalSql = builder.getStatement(getTableMetadata()).getSql();
        }
        log.info("Preparing statement: " + finalSql);
        final PreparedStatement statement;
        if (hasParameters()) {
            //noinspection unchecked
            statement = getPreparator().prepare(connection, getTableMetadata(), (Map<String, Object>) value, finalSql);
        } else {
            statement = prepare(connection);
        }
        return (CallableStatement) statement;
    }

    @Override
    protected StatementPreparator getPreparator() {
        return preparator;
    }

}
