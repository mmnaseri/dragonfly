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

package com.agileapes.dragonfly.metadata;

import java.util.Collection;

/**
 * Interface encapsulating properties of a constraint on any given table.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:09)
 */
public interface ConstraintMetadata extends Metadata {

    /**
     * @return the (globally) unique name of the constraint, in the current persistence
     * context
     */
    String getName();

    /**
     * @return the table for which the constraint has been defined
     */
    TableMetadata<?> getTable();

    /**
     * @return the columns the constraint refers to. Integrity of data access definition
     * requires that the columns and the constraint should all share the same table.
     */
    Collection<ColumnMetadata> getColumns();

}
