package com.agileapes.dragonfly.metadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 19:37)
 */
public interface TableMetadataInterceptor {

    <E> TableMetadata<E> intercept(TableMetadata<E> tableMetadata);

}
