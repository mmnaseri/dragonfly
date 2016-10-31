/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mmnaseri.dragonfly.error;

/**
 * This is the base class for all database related errors thrown by this framework. This ensures
 * that all errors are easily catch-able by hierarchical catch statements.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/8/29, 14:38)
 */
public abstract class DatabaseError extends RuntimeException {

    /**
     * Instantiates the error by passing a message, and assuming that this is the root cause
     * of the problem.
     * @param message    the error message
     */
    public DatabaseError(String message) {
        super(message);
    }

    /**
     * Instantiates the error, while also specifying the underlying cause of the problem
     * @param message    the error message
     * @param cause      the cause of the error
     */
    public DatabaseError(String message, Throwable cause) {
        super(message, cause);
    }
}
