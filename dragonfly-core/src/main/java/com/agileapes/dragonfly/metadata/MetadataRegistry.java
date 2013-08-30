package com.agileapes.dragonfly.metadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/30, 14:35)
 */
public interface MetadataRegistry {

    <E> TableMetadata<E> getTableMetadata(Class<E> entityType);

    <E> void addTableMetadata(Class<E> entityType, TableMetadata<E> tableMetadata);

    boolean contains(Class<?> entityType);

}
