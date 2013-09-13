package com.agileapes.dragonfly.entity;

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.Collection;
import java.util.Map;

/**
 * This interface will convert a given map into the expected entity type
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:13)
 */
public interface MapEntityCreator {

    /**
     * Returns an instance of the given entity with its properties set to the
     * map's contents
     * @param tableMetadata    the table metadata
     * @param values           the map of values
     * @param <E>              the type of the entity
     * @return the entity
     */
    <E> E fromMap(TableMetadata<E> tableMetadata, Map<String, Object> values);

    /**
     * Sets the properties of the given entity object to the values represented in
     * the map
     * @param entity     the entity to be set
     * @param columns    the columns of the entity
     * @param values     the map of values
     * @param <E>        the type of the entity
     * @return the entity
     */
    <E> E fromMap(E entity, Collection<ColumnMetadata> columns, Map<String, Object> values);

}
