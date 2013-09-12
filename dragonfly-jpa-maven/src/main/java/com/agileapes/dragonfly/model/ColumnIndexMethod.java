package com.agileapes.dragonfly.model;

import com.agileapes.couteau.freemarker.api.Invokable;
import com.agileapes.couteau.freemarker.model.TypedMethodModel;
import com.agileapes.dragonfly.metadata.ColumnMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 13:54)
 */
public class ColumnIndexMethod extends TypedMethodModel {

    private final Map<ColumnMetadata, Integer> indices = new HashMap<ColumnMetadata, Integer>();

    @Invokable
    public int getIndex(ColumnMetadata columnMetadata) {
        if (!indices.containsKey(columnMetadata)) {
            indices.put(columnMetadata, indices.size());
        }
        return indices.get(columnMetadata);
    }

}
