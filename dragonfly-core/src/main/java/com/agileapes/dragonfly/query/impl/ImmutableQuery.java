package com.agileapes.dragonfly.query.impl;

import com.agileapes.dragonfly.query.Query;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 18:02)
 */
public class ImmutableQuery implements Query {

    private final String sql;
    private final boolean dynamic;

    public ImmutableQuery(String sql, boolean dynamic) {
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
