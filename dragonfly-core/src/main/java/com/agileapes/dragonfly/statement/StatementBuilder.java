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

import com.agileapes.dragonfly.metadata.Metadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

/**
 * This interface allows for encapsulation of the behaviour of a class that is supposed to act as a
 * factory for generation of certain statement type. This allows for pre-configuration of factories
 * for statements that are generically the same, but are expected to work out-of-the-box for specific
 * entity types.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/1, 1:21)
 */
public interface StatementBuilder {

    /**
     * Returns the statement object using the given table metadata as the center-piece, while using an
     * additional metadata object as an auxiliary piece of data that could help with the final processing
     * of the statement before it can be executed by the underlying database server
     * @param tableMetadata    the table metadata
     * @param metadata         the auxiliary metadata
     * @return the statement object ready for preparation
     */
    Statement getStatement(TableMetadata<?> tableMetadata, Metadata metadata);

    /**
     * Returns the statement object using the given table metadata as the center-piece
     * @param tableMetadata    the table metadata
     * @return the statement object ready for preparation
     */
    Statement getStatement(TableMetadata<?> tableMetadata);

}
