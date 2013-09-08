package com.agileapes.dragonfly.tools;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.metadata.ColumnMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 17:59)
 */
public class SequenceColumnFilter implements Filter<ColumnMetadata> {

    @Override
    public boolean accepts(ColumnMetadata columnMetadata) {
        return columnMetadata.getGenerationType() != null;
    }

}
