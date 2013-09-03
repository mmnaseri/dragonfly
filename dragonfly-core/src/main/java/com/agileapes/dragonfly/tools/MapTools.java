package com.agileapes.dragonfly.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 17:17)
 */
public abstract class MapTools {

    public static <E> Map<String, E> prefixKeys(Map<String, E> map, String prefix) {
        final HashMap<String, E> result = new HashMap<String, E>();
        for (Map.Entry<String, E> entry : map.entrySet()) {
            result.put(prefix + entry.getKey(), entry.getValue());
        }
        return result;
    }

}
