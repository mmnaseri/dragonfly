package com.agileapes.dragonfly.cg;

import com.agileapes.couteau.enhancer.api.ClassEnhancer;
import com.agileapes.couteau.enhancer.api.NamingPolicy;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This naming policy uses a static naming scheme of enhanced classes to enable the framework to recognize enhanced
 * classes more easily.
 *
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
    public String getClassName(Class<?> originalClass, ClassEnhancer<?> classEnhancer) {
        return originalClass.getCanonicalName() + "$ENHANCED$" + name + "$" + index.getAndIncrement();
    }

}
