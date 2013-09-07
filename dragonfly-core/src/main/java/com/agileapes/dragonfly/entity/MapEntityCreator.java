package com.agileapes.dragonfly.entity;

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.Collection;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:13)
 */
public interface MapEntityCreator {

    <E> E fromMap(TableMetadata<E> tableMetadata, Map<String, Object> values);

    <E> E fromMap(E entity, Collection<ColumnMetadata> columns, Map<String, Object> values);

}
