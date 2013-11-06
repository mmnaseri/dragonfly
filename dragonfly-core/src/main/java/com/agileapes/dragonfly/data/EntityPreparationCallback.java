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
