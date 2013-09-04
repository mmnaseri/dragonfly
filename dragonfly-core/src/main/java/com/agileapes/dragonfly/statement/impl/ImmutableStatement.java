package com.agileapes.dragonfly.statement.impl;

import com.agileapes.dragonfly.statement.Statement;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 18:02)
 */
public class ImmutableStatement implements Statement {

    private final String sql;
    private final boolean dynamic;

    public ImmutableStatement(String sql, boolean dynamic) {
        this.sql = sql;
        this.dynamic = dynamic;
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
