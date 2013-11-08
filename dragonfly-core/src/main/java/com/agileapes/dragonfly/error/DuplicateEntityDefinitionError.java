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

package com.agileapes.dragonfly.error;

/**
 * This error means that a duplicate definition was added to the entity definition context
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/30, 15:51)
 */
public class DuplicateEntityDefinitionError extends DatabaseError {

    private final Class<?> entityType;

    public DuplicateEntityDefinitionError(Class<?> entityType) {
        super("More than one definition exists for entity: " + entityType.getCanonicalName());
        this.entityType = entityType;
    }

    public Class<?> getEntityType() {
        return entityType;
    }
}
