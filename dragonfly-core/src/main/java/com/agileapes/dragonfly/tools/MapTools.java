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

package com.agileapes.dragonfly.tools;

import java.util.*;

/**
 * Utility for working with and creating maps
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 17:17)
 */
public abstract class MapTools {

    /**
     * Given a map with String keys, will return a map with the keys prefixed and agreeing with the key-value
     * pairs of the original map
     * @param map       the original map
     * @param prefix    prefix to all keys
     * @param <E>       the type of the values
     * @return the prefixed map
     */
    public static <E> Map<String, E> prefixKeys(Map<String, E> map, String prefix) {
        final HashMap<String, E> result = new HashMap<String, E>();
        for (Map.Entry<String, E> entry : map.entrySet()) {
            result.put(prefix + entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Returns a map wrapper for the given key and value types
     * @param keyType      the key type
     * @param valueType    the value type
     * @param <K>          the key type parameter
     * @param <V>          the value type parameter
     * @return the map builder
     */
    public static <K, V> MapBuilder<K, V> map(Class<K> keyType, Class<V> valueType) {
        return new MapBuilder<K, V>();
    }

    public static class MapBuilder<K, V> {

        private final List<K> keys = new ArrayList<K>();

        /**
         * Adds keys to the map
         * @param keys    the keys to be added
         * @return the map builder
         */
        public MapBuilder<K, V> keys(K... keys) {
            Collections.addAll(this.keys, keys);
            return this;
        }

        /**
         * Associates keys previously added via {@link #keys(Object[])} with values
         * @param values    the values to add to the map
         * @return the actual map
         */
        public Map<K, V> values(V... values) {
            final HashMap<K, V> map = new HashMap<K, V>();
            for (int i = 0; i < keys.size(); i++) {
                K key = keys.get(i);
                map.put(key, values[i]);
            }
            return map;
        }

    }

}
