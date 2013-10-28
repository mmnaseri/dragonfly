package com.agileapes.dragonfly.tools;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.metadata.impl.ResolvedRepresentationColumnMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/3, 10:01)
 */
public class ColumnActualPropertyFilter implements Filter<ResolvedRepresentationColumnMetadata> {

    private final String actualProperty;

    public ColumnActualPropertyFilter(String actualProperty) {
        this.actualProperty = actualProperty;
    }

    @Override
    public boolean accepts(ResolvedRepresentationColumnMetadata item) {
        return actualProperty.equals(item.getActualProperty());
    }
}
