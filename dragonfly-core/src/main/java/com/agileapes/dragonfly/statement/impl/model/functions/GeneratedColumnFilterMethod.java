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

import com.agileapes.couteau.freemarker.model.FilteringMethodModel;
import com.agileapes.dragonfly.error.MetadataCollectionError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.tools.ColumnNameFilter;

import java.util.Collection;
import java.util.HashSet;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 17:48)
 */
public class GeneratedColumnFilterMethod extends FilteringMethodModel<ColumnMetadata> {

    private final Collection<ColumnMetadata> keys = new HashSet<ColumnMetadata>();

    public GeneratedColumnFilterMethod(TableMetadata<?> tableMetadata) {
        try {
            final Collection<ColumnMetadata> columns = tableMetadata.getPrimaryKey().getColumns();
            for (ColumnMetadata column : columns) {
                if (column.getGenerationType() != null) {
                    keys.add(column);
                }
            }
        } catch (MetadataCollectionError ignored) {}
    }

    @Override
    protected boolean filter(ColumnMetadata columnMetadata) {
        return !with(keys).keep(new ColumnNameFilter(columnMetadata.getName())).isEmpty();
    }

}
