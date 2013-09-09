package com.agileapes.dragonfly.security.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.security.SubjectFilter;

import java.lang.reflect.Method;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 17:01)
 */
public class MethodSubjectFilter implements SubjectFilter<MethodSubject> {

    private final Filter<? super Method> filter;

    public MethodSubjectFilter(Filter<? super Method> filter) {
        this.filter = filter;
    }

    @Override
    public boolean accepts(MethodSubject item) {
        return filter.accepts(item.getMethod());
    }

}
