package com.agileapes.dragonfly.data.impl;

import java.sql.PreparedStatement;

/**
 * This class holds information about a certain batch operation. The SQL field
 * ensures that the equality of batch operations is based on the SQL statement
 * being changed.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/25, 22:06)
 */
class BatchOperationDescriptor {

    private final PreparedStatement preparedStatement;
    private final String sql;

    BatchOperationDescriptor(PreparedStatement preparedStatement, String sql) {
        this.preparedStatement = preparedStatement;
        this.sql = sql;
    }

    PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    String getSql() {
        return sql;
    }

}
