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

import com.mmnaseri.dragonfly.metadata.ColumnMetadata;
import com.mmnaseri.dragonfly.metadata.TableMetadata;
import com.mmnaseri.dragonfly.tools.SynchronizedIdentifierDispenser;

import java.util.Arrays;
import java.util.HashSet;

/**
 * This class denotes a foreign key constraint, and as such can only take one column as the
 * columns to which it applies.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/8/29, 14:28)
 */
public class ForeignKeyConstraintMetadata extends AbstractConstraintMetadata {

    private static SynchronizedIdentifierDispenser<TableMetadata<?>> dispenser = new SynchronizedIdentifierDispenser<TableMetadata<?>>();

    public ForeignKeyConstraintMetadata(TableMetadata table, ColumnMetadata column) {
        super(table, new HashSet<ColumnMetadata>(Arrays.asList(column)));
    }

    public ColumnMetadata getColumn() {
        return getColumns().iterator().next();
    }

    @Override
    protected String getNameSuffix() {
        return "fk" + dispenser.getIdentifier(getTable());
    }

}
