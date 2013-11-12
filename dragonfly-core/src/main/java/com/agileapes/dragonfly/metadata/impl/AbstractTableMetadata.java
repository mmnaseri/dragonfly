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

package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.TableMetadata;

/**
 * This class supports the basic property of the table metadata class, {@link #getEntityType()}
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:47)
 */
public abstract class AbstractTableMetadata<E> implements TableMetadata<E> {

    private final Class<E> entityType;

    public AbstractTableMetadata(Class<E> entityType) {
        this.entityType = entityType;
    }

    @Override
    public Class<E> getEntityType() {
        return entityType;
    }

}
