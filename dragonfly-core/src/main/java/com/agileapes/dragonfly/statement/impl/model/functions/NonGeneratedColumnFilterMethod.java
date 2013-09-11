package com.agileapes.dragonfly.statement.impl.model.functions;

import com.agileapes.couteau.freemarker.api.Invokable;
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
 * @since 1.0 (2013/9/1, 13:46)
 */
public class NonGeneratedColumnFilterMethod extends FilteringMethodModel<ColumnMetadata> {

    private final Collection<ColumnMetadata> keys = new HashSet<ColumnMetadata>();

    public NonGeneratedColumnFilterMethod(TableMetadata<?> tableMetadata) {
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
    @Invokable
    protected boolean filter(ColumnMetadata columnMetadata) {
        return with(keys).keep(new ColumnNameFilter(columnMetadata.getName())).isEmpty();
    }

}