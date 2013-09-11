package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.CascadeMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 0:16)
 */
public class ImmutableCascadeMetadata implements CascadeMetadata {

    private final boolean persist;
    private final boolean merge;
    private final boolean remove;
    private final boolean refresh;

    public ImmutableCascadeMetadata(boolean persist, boolean merge, boolean remove, boolean refresh) {
        this.persist = persist;
        this.merge = merge;
        this.remove = remove;
        this.refresh = refresh;
    }

    @Override
    public boolean cascadePersist() {
        return persist;
    }

    @Override
    public boolean cascadeMerge() {
        return merge;
    }

    @Override
    public boolean cascadeRemove() {
        return remove;
    }

    @Override
    public boolean cascadeRefresh() {
        return refresh;
    }
}
