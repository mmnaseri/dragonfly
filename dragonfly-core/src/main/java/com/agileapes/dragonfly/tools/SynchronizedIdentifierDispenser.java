package com.agileapes.dragonfly.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:23)
 */
public class SynchronizedIdentifierDispenser<E> {

    private final Map<E, Long> data = new HashMap<E, Long>();

    public synchronized long getIdentifier(E target) {
        final Long value;
        if (data.containsKey(target)) {
            value = data.get(target);
        } else {
            value = 0L;
        }
        data.put(target, value + 1);
        return value;
    }

}
