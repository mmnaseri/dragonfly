package com.agileapes.dragonfly.statement.impl.model.functions;

import com.agileapes.couteau.freemarker.api.Invokable;
import com.agileapes.couteau.freemarker.model.FilteringMethodModel;
import com.agileapes.dragonfly.metadata.ColumnMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/28, 22:41)
 */
public class VersionColumnSelectorMethod extends FilteringMethodModel<ColumnMetadata> {

    @Invokable
    @Override
    protected boolean filter(ColumnMetadata columnMetadata) {
        return columnMetadata.equals(columnMetadata.getTable().getVersionColumn());
    }

}
