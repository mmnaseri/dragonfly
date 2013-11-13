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
 * This error indicates that the entity context has been initialized once. This error helps
 * prevent hard-to-trace errors due to overriding injected dependencies from happening.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/11/13, 10:32)
 */
public class EntityContextReInitializationError extends DatabaseError {

    public EntityContextReInitializationError() {
        super("The entity context has been initialized once, and as such cannot be initialized again.");
    }

}
