package com.agileapes.dragonfly.sample.assets;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/5, 10:45)
 */
@SuppressWarnings("NullableProblems")
@Repository
public class Memory implements Map<Long, Memorable> {

    private final Map<Long, Memorable> map = new ConcurrentHashMap<Long, Memorable>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Memorable get(Object key) {
        return map.get(key);
    }

    @Override
    public Memorable put(Long key, Memorable value) {
        return map.put(key, value);
    }

    @Override
    public Memorable remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Memorable> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<Long> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Memorable> values() {
        return map.values();
    }

    @Override
    public Set<Entry<Long,Memorable>> entrySet() {
        return map.entrySet();
    }

}
