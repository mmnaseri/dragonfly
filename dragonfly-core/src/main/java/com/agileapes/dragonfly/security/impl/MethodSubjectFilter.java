package com.agileapes.dragonfly.security.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.enhancer.api.MethodDescriptor;
import com.agileapes.dragonfly.security.SubjectFilter;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 17:01)
 */
public class MethodSubjectFilter implements SubjectFilter<MethodSubject> {

    private final Filter<? super MethodDescriptor> filter;

    public MethodSubjectFilter(Filter<? super MethodDescriptor> filter) {
        this.filter = filter;
    }

    @Override
    public boolean accepts(MethodSubject item) {
        return filter.accepts(item.getMethod());
    }

}
