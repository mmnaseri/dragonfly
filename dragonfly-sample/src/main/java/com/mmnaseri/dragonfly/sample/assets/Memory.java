/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mmnaseri.dragonfly.sample.assets;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
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
