/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mmnaseri.dragonfly.metadata.impl;

import com.mmnaseri.dragonfly.error.MetadataCollectionError;
import com.mmnaseri.dragonfly.error.UnresolvedTableMetadataError;
import com.mmnaseri.dragonfly.metadata.ColumnMetadata;
import com.mmnaseri.dragonfly.metadata.ConstraintMetadata;
import com.mmnaseri.dragonfly.metadata.TableMetadata;
import com.mmnaseri.dragonfly.tools.DatabaseUtils;

import java.util.Collection;

/**
 * This class contains the two basic traits common to all constraints, {@link #getTable()}
 * and {@link #getColumns()}.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/8/29, 14:11)
 */
public abstract class AbstractConstraintMetadata implements ConstraintMetadata {

    private String name;
    private TableMetadata table;
    private final Collection<ColumnMetadata> columns;

    public AbstractConstraintMetadata(TableMetadata table, Collection<ColumnMetadata> columns) {
        this.columns = columns;
        setTable(table);
    }

    private String produceName() {
        return DatabaseUtils.unify(getTable().getName()) + "_" + getNameSuffix();
    }

    protected abstract String getNameSuffix();

    @Override
    public String getName() {
        if (name == null) {
            throw new MetadataCollectionError("Constraint name not available", new UnresolvedTableMetadataError(table.getEntityType()));
        }
        return name;
    }

    @Override
    public TableMetadata<?> getTable() {
        return table;
    }

    @Override
    public Collection<ColumnMetadata> getColumns() {
        return columns;
    }

    public void setTable(TableMetadata<?> table) {
        this.table = table;
        if (table != null) {
            this.name = produceName();
        }
    }

}
