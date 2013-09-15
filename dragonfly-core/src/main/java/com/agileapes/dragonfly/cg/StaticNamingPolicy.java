package com.agileapes.dragonfly.cg;

import com.agileapes.couteau.enhancer.api.ClassEnhancer;
import com.agileapes.couteau.enhancer.api.NamingPolicy;
import com.agileapes.dragonfly.tools.SynchronizedIdentifierDispenser;

/**
 * This naming policy uses a static naming scheme of enhanced classes to enable the framework to recognize enhanced
 * classes more easily.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 16:29)
 */
public class StaticNamingPolicy implements NamingPolicy {

    private final String name;
    private final static SynchronizedIdentifierDispenser<Class<?>> IDENTIFIER_DISPENSER = new SynchronizedIdentifierDispenser<Class<?>>();

    public StaticNamingPolicy(String name) {
        this.name = name;
    }

    @Override
    public String getClassName(Class<?> originalClass, ClassEnhancer<?> classEnhancer) {
        return originalClass.getCanonicalName() + "$ENHANCED$" + name + "$" + IDENTIFIER_DISPENSER.getIdentifier(originalClass);
    }

}
