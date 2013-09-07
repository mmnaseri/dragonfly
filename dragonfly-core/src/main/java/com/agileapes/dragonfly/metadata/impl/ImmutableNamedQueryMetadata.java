package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.NamedQueryMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/7, 12:35)
 */
public class ImmutableNamedQueryMetadata implements NamedQueryMetadata {

    private final String name;
    private final String query;

    public ImmutableNamedQueryMetadata(String name, String query) {
        this.name = name;
        this.query = query;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getQuery() {
        return query;
    }
}
