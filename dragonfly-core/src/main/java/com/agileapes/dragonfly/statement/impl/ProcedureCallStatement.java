package com.agileapes.dragonfly.statement.impl;

import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.entity.EntityMapCreator;
import com.agileapes.dragonfly.entity.StatementPreparator;
import com.agileapes.dragonfly.entity.impl.DefaultStatementPreparator;
import com.agileapes.dragonfly.error.StatementError;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.statement.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
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
            throw new StatementError("Failed to prepare statement through connection", e);
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
