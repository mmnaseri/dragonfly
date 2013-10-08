package com.agileapes.dragonfly.ext.impl.parser;

import com.agileapes.couteau.basics.api.Filter;

import java.util.Arrays;
import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/7, 23:45)
 */
public class OrTypeFilter implements Filter<Class<?>> {

    private final List<Filter<Class<?>>> filters;

    public OrTypeFilter(Filter<Class<?>>... filters) {
        this(Arrays.asList(filters));
    }

    public OrTypeFilter(List<Filter<Class<?>>> filters) {
        this.filters = filters;
    }


    @Override
    public boolean accepts(Class<?> item) {
        for (Filter<Class<?>> filter : filters) {
            if (filter.accepts(item)) {
                return true;
            }
        }
        return false;
    }
}
