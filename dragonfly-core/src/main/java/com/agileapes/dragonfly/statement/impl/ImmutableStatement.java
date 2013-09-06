package com.agileapes.dragonfly.statement.impl;

import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.entity.EntityMapCreator;
import com.agileapes.dragonfly.entity.StatementPreparator;
import com.agileapes.dragonfly.entity.impl.DefaultEntityMapCreator;
import com.agileapes.dragonfly.entity.impl.DefaultStatementPreparator;
import com.agileapes.dragonfly.error.StatementError;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.statement.Statement;
import com.agileapes.dragonfly.statement.StatementType;
import com.agileapes.dragonfly.tools.MapTools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 18:02)
 */
public class ImmutableStatement implements Statement {

    private final TableMetadata<?> tableMetadata;
    private final DatabaseDialect dialect;
    private final String sql;
    private final boolean dynamic;
    private final boolean parameters;
    private final StatementType type;
    private final StatementPreparator preparator;

    public ImmutableStatement(TableMetadata<?> tableMetadata, DatabaseDialect dialect, String sql, boolean dynamic, boolean parameters, StatementType type) {
        this.tableMetadata = tableMetadata;
        this.dialect = dialect;
        this.sql = sql;
        this.dynamic = dynamic;
        this.parameters = parameters;
        this.type = type;
        preparator = parameters ? new DefaultStatementPreparator() : null;
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    @Override
    public boolean hasParameters() {
        return parameters;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public StatementType getType() {
        return type;
    }

    @Override
    public TableMetadata<?> getTableMetadata() {
        return tableMetadata;
    }

    @Override
    public PreparedStatement prepare(Connection connection) {
        System.out.println(sql);
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new StatementError("Failed to prepare statement through connection", e);
        }
    }

    @Override
    public PreparedStatement prepare(Connection connection, Object value) {
        return prepare(connection, value, null);
    }

    @Override
    public PreparedStatement prepare(Connection connection, Object value, Object replacement) {
        String finalSql = sql;
        if (isDynamic()) {
            final FreemarkerSecondPassStatementBuilder builder = new FreemarkerSecondPassStatementBuilder(this, dialect, value);
            finalSql = builder.getStatement(tableMetadata).getSql();
        }
        final PreparedStatement statement;
        System.out.println(finalSql);
        if (hasParameters()) {
            EntityMapCreator mapCreator = new DefaultEntityMapCreator();
            //noinspection unchecked
            final Map<String, Object> values = mapCreator.toMap((TableMetadata<Object>) tableMetadata, value);
            final Map<String,Object> map = MapTools.prefixKeys(values, "value.");
            if (replacement != null) {
                map.putAll(MapTools.prefixKeys(values, "old."));
                //noinspection unchecked
                map.putAll(MapTools.prefixKeys(mapCreator.toMap((TableMetadata<Object>) tableMetadata, replacement), "new."));
            }
            statement = preparator.prepare(connection, tableMetadata, map, finalSql);
        } else {
            try {
                statement = connection.prepareStatement(finalSql);
            } catch (SQLException e) {
                throw new StatementError("Failed to prepare statement through connection", e);
            }
        }
        return statement;
    }


}
