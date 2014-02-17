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

package com.agileapes.dragonfly.statement.impl.model.functions;

import com.agileapes.couteau.freemarker.api.Invokable;
import com.agileapes.couteau.freemarker.model.FilteringMethodModel;
import com.agileapes.dragonfly.metadata.ColumnMetadata;

/**
 * Picks out columns that are not version columns
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/28, 22:43)
 * @see com.agileapes.dragonfly.statement.impl.model.functions.VersionColumnSelectorMethod
 */
public class NonVersionColumnSelectorMethod extends FilteringMethodModel<ColumnMetadata> {

    @Invokable
    @Override
    protected boolean filter(ColumnMetadata columnMetadata) {
        return !columnMetadata.equals(columnMetadata.getTable().getVersionColumn());
    }

}
