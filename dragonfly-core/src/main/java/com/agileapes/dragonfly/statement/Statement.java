/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

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
