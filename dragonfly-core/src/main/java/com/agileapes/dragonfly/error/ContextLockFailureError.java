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
 * This error signifies a problem in nested calls to lock/unlock operations associated with
 * {@link com.agileapes.dragonfly.entity.EntityInitializationContext} instances.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/21, 23:22)
 */
public class ContextLockFailureError extends DataAccessError {

    public ContextLockFailureError() {
        super("Initialization context lock has been released without being captured first.");
    }

}
