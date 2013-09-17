package com.agileapes.dragonfly.ext;

import com.agileapes.couteau.basics.api.Filter;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 14:55)
 */
public class ExtensionResolver {

    public Filter<Class<?>> resolve(String extension) {
        return new Filter<Class<?>>() {
            @Override
            public boolean accepts(Class<?> item) {
                return true;
            }
        };
    }

}
