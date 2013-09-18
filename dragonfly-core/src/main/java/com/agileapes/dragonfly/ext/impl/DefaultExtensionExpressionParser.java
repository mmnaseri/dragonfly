package com.agileapes.dragonfly.ext.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.ext.ExtensionExpressionParser;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/18, 15:53)
 */
public class DefaultExtensionExpressionParser implements ExtensionExpressionParser {
    @Override
    public Filter<Class<?>> parse(String expression) {
        return new Filter<Class<?>>() {
            @Override
            public boolean accepts(Class<?> item) {
                return true;
            }
        };
    }
}
