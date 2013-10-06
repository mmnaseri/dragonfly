package com.agileapes.dragonfly.entity;

import com.agileapes.dragonfly.metadata.ColumnMetadata;

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
     * Sets the properties of the given entity object to the values represented in
     * the map
     *
     * @param entity     the entity to be set
     * @param columns    the columns of the entity
     * @param values     the map of values
     * @return the entity
     */
    <E> E fromMap(E entity, Collection<ColumnMetadata> columns, Map<String, Object> values);

}
