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

package com.mmnaseri.dragonfly.runtime.analysis.impl;

import com.mmnaseri.dragonfly.metadata.TableMetadata;
import com.mmnaseri.dragonfly.runtime.analysis.IssueTarget;
import com.mmnaseri.dragonfly.tools.DatabaseUtils;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/3/17 AD, 15:36)
 */
public class TableIssueTarget implements IssueTarget<TableMetadata<?>> {

    private final TableMetadata<?> tableMetadata;

    public TableIssueTarget(TableMetadata<?> tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    @Override
    public TableMetadata<?> getTarget() {
        return tableMetadata;
    }

    @Override
    public String toString() {
        return "table " + DatabaseUtils.qualifyTable(tableMetadata, '\'', '.') + " defined for entity '" + tableMetadata.getEntityType().getCanonicalName() + "'";
    }

}
