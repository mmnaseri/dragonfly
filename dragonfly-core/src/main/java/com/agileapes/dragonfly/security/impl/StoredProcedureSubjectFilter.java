package com.agileapes.dragonfly.security.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.metadata.StoredProcedureMetadata;
import com.agileapes.dragonfly.security.SubjectFilter;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 2:52)
 */
public class StoredProcedureSubjectFilter implements SubjectFilter<StoredProcedureSubject> {

    private final Filter<? super StoredProcedureMetadata> filter;

    public StoredProcedureSubjectFilter(Filter<? super StoredProcedureMetadata> filter) {
        this.filter = filter;
    }

    @Override
    public boolean accepts(StoredProcedureSubject item) {
        return filter.accepts(item.getProcedureMetadata());
    }
}
