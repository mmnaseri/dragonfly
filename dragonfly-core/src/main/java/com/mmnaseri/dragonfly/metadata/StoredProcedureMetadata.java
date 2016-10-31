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

package com.mmnaseri.dragonfly.metadata;

import java.util.List;

/**
 * <p>The metadata for a procedure that is callable from the application is available through
 * this interface. Each procedure is called to a single table and entity, even though they
 * must clearly specify a result type that might not be the same as the entity to which they
 * are statically bound.</p>
 *
 * <p>This is to bind each procedure to the same catalog and schema as the entity, and prevent
 * dangling procedure identifiers that do not belong to a pre-designated schema.</p>
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/10, 0:44)
 */
public interface StoredProcedureMetadata extends Metadata {

    /**
     * @return the table to which this procedure is bound
     */
    TableMetadata<?> getTable();

    /**
     * @return the name of the procedure, which must be unique across the persistence unit.
     */
    String getName();

    /**
     * @return the result type for the procedure, which can be {@code void.class} in case it
     * does not return any values. If, however, it is not set to {@code void.class}, it means
     * that it must be either another well-defined entity, or it must be a partial entity,
     * clearly bound to a single named query across the whole persistence unit.
     */
    Class<?> getResultType();

    /**
     * @return the parameters to the procedure call
     */
    List<ParameterMetadata> getParameters();

    /**
     * @return {@code true} in case the result type for the procedure call is partial
     */
    boolean isPartial();

}
