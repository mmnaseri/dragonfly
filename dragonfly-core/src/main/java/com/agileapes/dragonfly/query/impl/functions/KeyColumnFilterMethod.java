package com.agileapes.dragonfly.query.impl.functions;

import com.agileapes.couteau.freemarker.model.FilteringMethodModel;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.tools.ColumnNameFilter;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 17:48)
 */
public class KeyColumnFilterMethod extends FilteringMethodModel<ColumnMetadata> {

    private final TableMetadata<?> tableMetadata;

    public KeyColumnFilterMethod(TableMetadata<?> tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    @Override
    protected boolean filter(ColumnMetadata item) {
        return !with(tableMetadata.getPrimaryKey().getColumns()).keep(new ColumnNameFilter(item.getName())).isEmpty();
    }

}
