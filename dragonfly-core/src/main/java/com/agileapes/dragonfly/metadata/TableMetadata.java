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

import com.agileapes.dragonfly.metadata.impl.PrimaryKeyConstraintMetadata;

import java.util.Collection;
import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:06)
 */
public interface TableMetadata<E> extends Metadata {

    Class<E> getEntityType();

    String getName();

    String getSchema();

    Collection<ConstraintMetadata> getConstraints();

    PrimaryKeyConstraintMetadata getPrimaryKey();

    <C extends ConstraintMetadata> Collection<C> getConstraints(Class<C> constraintType);

    Collection<ReferenceMetadata<E, ?>> getForeignReferences();

    Collection<ColumnMetadata> getColumns();

    boolean hasPrimaryKey();

    Collection<NamedQueryMetadata> getNamedQueries();

    Collection<SequenceMetadata> getSequences();

    Collection<StoredProcedureMetadata> getProcedures();

    ColumnMetadata getVersionColumn();

    List<OrderMetadata> getOrdering();

}
