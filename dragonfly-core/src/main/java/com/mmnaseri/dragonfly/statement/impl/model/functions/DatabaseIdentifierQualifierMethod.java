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

package com.mmnaseri.dragonfly.statement.impl.model.functions;

import com.mmnaseri.couteau.freemarker.api.Invokable;
import com.mmnaseri.couteau.freemarker.model.TypedMethodModel;
import com.mmnaseri.dragonfly.dialect.DatabaseDialect;
import com.mmnaseri.dragonfly.metadata.ColumnMetadata;
import com.mmnaseri.dragonfly.metadata.StoredProcedureMetadata;
import com.mmnaseri.dragonfly.metadata.TableMetadata;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Returns the qualified name of the database elements.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/1, 1:38)
 */
public class DatabaseIdentifierQualifierMethod extends TypedMethodModel {

    private final DatabaseDialect dialect;
    private final EscapeMethod escape;

    public DatabaseIdentifierQualifierMethod(DatabaseDialect dialect) {
        this.escape = new EscapeMethod(dialect.getIdentifierEscapeCharacter());
        this.dialect = dialect;
    }

    @Invokable
    public String getTableName(TableMetadata<?> tableMetadata) {
        String name = escape.escape(tableMetadata.getName());
        if (tableMetadata.getSchema() != null && !tableMetadata.getSchema().isEmpty()) {
            name = escape.escape(tableMetadata.getSchema()) + dialect.getSchemaSeparator() + name;
        }
        return name;
    }

    @Invokable
    public String getColumnName(ColumnMetadata columnMetadata) {
        String name = escape.escape(columnMetadata.getName());
        if (columnMetadata.getTable() != null) {
            name = getTableName(columnMetadata.getTable()) + dialect.getSchemaSeparator() + name;
        }
        return name;
    }

    @Invokable
    public String getProcedureName(StoredProcedureMetadata procedureMetadata) {
        String name = escape.escape(procedureMetadata.getName());
        if (procedureMetadata.getTable() != null && procedureMetadata.getTable().getSchema() != null && !procedureMetadata.getTable().getSchema().isEmpty()) {
            name = escape.escape(procedureMetadata.getTable().getSchema()) + dialect.getSchemaSeparator() + name;
        }
        return name;
    }

    @Invokable
    public Collection<String> qualifyCollection(Collection<?> collection) {
        final ArrayList<String> result = new ArrayList<String>();
        for (Object item : collection) {
            if (item instanceof TableMetadata<?>) {
                result.add(getTableName((TableMetadata<?>) item));
            } else if (item instanceof ColumnMetadata) {
                result.add(getColumnName((ColumnMetadata) item));
            } else if (item instanceof StoredProcedureMetadata) {
                result.add(getProcedureName((StoredProcedureMetadata) item));
            } else {
                throw new RuntimeException("Invalid item in collection. Items must be references to either columns, or tables.");
            }
        }
        return result;
    }

}
