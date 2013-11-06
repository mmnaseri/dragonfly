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
