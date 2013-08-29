package com.agileapes.dragonfly.tools;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.metadata.ConstraintMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:42)
 */
public class ConstraintTypeFilter implements Filter<ConstraintMetadata> {

    private final Class<? extends ConstraintMetadata> constraintType;

    public ConstraintTypeFilter(Class<? extends ConstraintMetadata> constraintType) {
        this.constraintType = constraintType;
    }

    @Override
    public boolean accepts(ConstraintMetadata constraintMetadata) {
        return constraintType.isInstance(constraintMetadata);
    }

}
