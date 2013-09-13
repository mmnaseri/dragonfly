package com.agileapes.dragonfly.entity;

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.Collection;
import java.util.Map;

/**
 * This interface will help with the process of converting entities into
 * maps from column name to property values.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:13)
 */
public interface EntityMapCreator {

    /**
     * Converts an entity with the given table metadata into a map
     * @param tableMetadata    the table metadata
     * @param entity           the entity to be converted
     * @param <E>              the type of the entity
     * @return the map corresponding with the input entity
     */
    <E> Map<String, Object> toMap(TableMetadata<E> tableMetadata, E entity);

    /**
     * Converts an entity with the given column metadata into a map
     * @param columns          the column metadata for the entity's columns
     * @param entity           the entity to be converted
     * @param <E>              the type of the entity
     * @return the map corresponding with the input entity
     */
    <E> Map<String, Object> toMap(Collection<ColumnMetadata> columns, E entity);

}
