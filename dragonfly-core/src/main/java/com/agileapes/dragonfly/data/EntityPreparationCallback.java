package com.agileapes.dragonfly.data;

import java.util.Map;

/**
 * A callback that is used to prepare an entity for use that has been just
 * fetched from the database
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/11/3, 13:00)
 */
public interface EntityPreparationCallback {

    /**
     * Is expected to properly prepare the entity based on the values through the map.
     * This includes fetching the related object graph.
     * @param entity    the entity to be prepared.
     * @param values    the actual values from the database.
     */
    void prepare(Object entity, Map<String, Object> values);

}
