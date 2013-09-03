package com.agileapes.dragonfly.query.impl.functions;

import com.agileapes.couteau.freemarker.api.Invokable;
import com.agileapes.couteau.freemarker.model.TypedMethodModel;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/1, 1:38)
 */
public class DatabaseIdentifierQualifier extends TypedMethodModel {

    private final DatabaseDialect dialect;
    private final EscapeMethod escape;

    public DatabaseIdentifierQualifier(DatabaseDialect dialect) {
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
    public Collection<String> qualifyCollection(Collection<?> collection) {
        final ArrayList<String> result = new ArrayList<String>();
        for (Object item : collection) {
            if (item instanceof TableMetadata<?>) {
                result.add(getTableName((TableMetadata<?>) item));
            } else if (item instanceof ColumnMetadata) {
                result.add(getColumnName((ColumnMetadata) item));
            } else {
                throw new Error("Invalid item in collection. Items must be references to either columns, or tables.");
            }
        }
        return result;
    }

}
