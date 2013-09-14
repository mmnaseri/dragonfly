package com.agileapes.dragonfly.statement;

import com.agileapes.dragonfly.entity.EntityMapCreator;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 17:58)
 */
public interface Statement {

    boolean isDynamic();

    boolean hasParameters();

    String getSql();

    StatementType getType();

    TableMetadata<?> getTableMetadata();

    PreparedStatement prepare(Connection connection);

    PreparedStatement prepare(Connection connection, EntityMapCreator mapCreator, Object value);

    PreparedStatement prepare(Connection connection, EntityMapCreator mapCreator, Object value, Object replacement);

}
