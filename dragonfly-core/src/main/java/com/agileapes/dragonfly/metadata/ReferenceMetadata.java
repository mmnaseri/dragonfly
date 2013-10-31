package com.agileapes.dragonfly.metadata;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 0:10)
 */
public interface ReferenceMetadata<S, D> extends Metadata {

    TableMetadata<S> getLocalTable();

    TableMetadata<D> getForeignTable();

    RelationType getRelationType();

    boolean isLazy();

    CascadeMetadata getCascadeMetadata();

    String getPropertyName();

    ColumnMetadata getForeignColumn();

    boolean isRelationOwner();

    Class<?> getDeclaringClass();

    List<OrderMetadata> getOrdering();

}
