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

package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.entity.EntityHandler;
import com.agileapes.dragonfly.entity.MapEntityCreator;
import com.agileapes.dragonfly.metadata.ColumnMetadata;

import java.util.Collection;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 5:35)
 */
public class DelegatingEntityCreator implements MapEntityCreator {
    
    private final EntityHandler<Object> handler;
    private final EntityContext entityContext;

    public DelegatingEntityCreator(EntityContext entityContext, EntityHandler<Object> handler) {
        this.handler = handler;
        this.entityContext = entityContext;
    }

    @Override
    public <E> E fromMap(E entity, Collection<ColumnMetadata> columns, Map<String, Object> values) {
        if (!handler.getEntityType().isInstance(entity)) {
            throw new UnsupportedOperationException();
        }
        //noinspection unchecked
        return (E) handler.fromMap(entity, values);
    }
}
