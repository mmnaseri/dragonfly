/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
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

package com.agileapes.dragonfly.metadata;

import com.agileapes.dragonfly.metadata.impl.PrimaryKeyConstraintMetadata;

import java.util.Collection;
import java.util.List;

/**
 * Interface allowing for definition and declaration of a table's properties, which
 * homes on the central assumption that each entity is bound exactly to one table metadata,
 * and vice versa.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:06)
 */
public interface TableMetadata<E> extends Metadata {

    /**
     * @return the type of the entity for which the table metadata is provided.
     */
    Class<E> getEntityType();

    /**
     * @return the name of the table, which must be unique across the persistence unit.
     */
    String getName();

    /**
     * @return the schema for the table
     */
    String getSchema();

    /**
     * @return constraints set on the table
     */
    Collection<ConstraintMetadata> getConstraints();

    /**
     * @return the primary key for the table
     * @throws com.agileapes.dragonfly.error.NoPrimaryKeyDefinedError if no primary key has been
     * defined for the table
     */
    PrimaryKeyConstraintMetadata getPrimaryKey();

    /**
     * Returns constraints of the given type
     * @param constraintType    the type of the constraint
     * @param <C>               the type parameter for the constraint
     * @return a (possibly empty) collection of all constraints of the given type
     */
    <C extends ConstraintMetadata> Collection<C> getConstraints(Class<C> constraintType);

    /**
     * @return the foreign references for the table
     */
    Collection<RelationMetadata<E, ?>> getForeignReferences();

    /**
     * @return the table's columns
     */
    Collection<ColumnMetadata> getColumns();

    /**
     * @return {@code true} if the table has a primary key
     */
    boolean hasPrimaryKey();

    /**
     * @return the named queries for the table
     */
    Collection<NamedQueryMetadata> getNamedQueries();

    /**
     * @return the sequences defined for this table
     */
    Collection<SequenceMetadata> getSequences();

    /**
     * @return all procedure calls available through this table
     */
    Collection<StoredProcedureMetadata> getProcedures();

    /**
     * @return the version column defined for optimistic locking through the table, or
     * {@code null} if no version column exists
     */
    ColumnMetadata getVersionColumn();

    /**
     * @return the default ordering of retrieved items from this table through default
     * data access retrieval methods
     */
    List<OrderMetadata> getOrdering();

}
