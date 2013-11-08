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

import java.io.Serializable;

/**
 * This error means that the key to the entity was not defined properly, and as such, more than one
 * object in the database responds to the given key
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/11/8, 17:31)
 */
public class ObjectKeyError extends EntityDefinitionError {

    public ObjectKeyError(Class<?> entityType, Serializable key) {
        super("More than one object in the underlying data source correspond to entity of type " + entityType.getCanonicalName() + " with key " + key);
    }

}
