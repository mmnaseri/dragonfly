package com.agileapes.dragonfly.query.impl.functions;

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
public class NonKeyColumnFilterMethod extends FilteringMethodModel<ColumnMetadata> {

    private final Collection<ColumnMetadata> keys = new HashSet<ColumnMetadata>();

    public NonKeyColumnFilterMethod(TableMetadata<?> tableMetadata) {
        try {
            keys.addAll(tableMetadata.getPrimaryKey().getColumns());
        } catch (MetadataCollectionError ignored) {}
    }

    @Override
    @Invokable
    protected boolean filter(ColumnMetadata columnMetadata) {
        return with(keys).keep(new ColumnNameFilter(columnMetadata.getName())).isEmpty();
    }

}
