package com.agileapes.dragonfly.metadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 0:08)
 */
public interface CascadeMetadata {

    boolean cascadePersist();

    boolean cascadeMerge();

    boolean cascadeRemove();

    boolean cascadeRefresh();

}
