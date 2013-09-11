package com.agileapes.dragonfly.metadata;

import com.agileapes.dragonfly.metadata.impl.PrimaryKeyConstraintMetadata;

import java.util.Collection;

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

}
