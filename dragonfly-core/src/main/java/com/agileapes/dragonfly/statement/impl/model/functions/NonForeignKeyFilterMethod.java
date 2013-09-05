package com.agileapes.dragonfly.statement.impl.model.functions;

import com.agileapes.couteau.freemarker.api.Invokable;
import com.agileapes.couteau.freemarker.model.FilteringMethodModel;
import com.agileapes.dragonfly.metadata.ColumnMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 14:12)
 */
public class NonForeignKeyFilterMethod extends FilteringMethodModel<ColumnMetadata> {

    @Invokable
    @Override
    protected boolean filter(ColumnMetadata item) {
        return item.getForeignReference() == null;
    }

}
