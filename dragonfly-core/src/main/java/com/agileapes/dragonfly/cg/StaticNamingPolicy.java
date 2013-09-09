package com.agileapes.dragonfly.cg;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 16:29)
 */
public class StaticNamingPolicy implements NamingPolicy {

    private final String name;
    private final AtomicInteger index = new AtomicInteger(1);

    public StaticNamingPolicy(String name) {
        this.name = name;
    }

    @Override
    public String getClassName(String prefix, String source, Object key, Predicate names) {
        return prefix + "$ENHANCED$" + name + "$" + index.getAndIncrement();
    }

}
