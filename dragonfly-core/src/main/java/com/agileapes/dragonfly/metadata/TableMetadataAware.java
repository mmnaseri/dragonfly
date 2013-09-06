package com.agileapes.dragonfly.metadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 19:29)
 */
public interface TableMetadataAware<E> {

    TableMetadata<E> getTableMetadata();

}
