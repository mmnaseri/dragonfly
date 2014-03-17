/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.tools;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.metadata.ConstraintMetadata;

/**
 * Filters table constraints by their type
 *
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
