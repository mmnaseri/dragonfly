package com.agileapes.dragonfly.statement.impl;

import com.agileapes.dragonfly.statement.Statement;
import com.agileapes.dragonfly.statement.StatementType;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 18:02)
 */
public class ImmutableStatement implements Statement {

    private final String sql;
    private final boolean dynamic;
    private final boolean parameters;
    private final StatementType type;

    public ImmutableStatement(String sql, boolean dynamic, boolean parameters, StatementType type) {
        this.sql = sql;
        this.dynamic = dynamic;
        this.parameters = parameters;
        this.type = type;
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

}
