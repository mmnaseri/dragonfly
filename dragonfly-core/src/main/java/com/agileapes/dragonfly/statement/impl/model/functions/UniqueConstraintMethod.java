package com.agileapes.dragonfly.statement.impl.model.functions;

import com.agileapes.couteau.freemarker.model.FilteringMethodModel;
import com.agileapes.dragonfly.metadata.ConstraintMetadata;
import com.agileapes.dragonfly.metadata.impl.UniqueConstraintMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/4, 16:21)
 */
public class UniqueConstraintMethod extends FilteringMethodModel<ConstraintMetadata> {

    @Override
    protected boolean filter(ConstraintMetadata item) {
        return item instanceof UniqueConstraintMetadata;
    }

}
