package com.agileapes.dragonfly.metadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 12:55)
 */
public interface MetadataResolver {

    <E> TableMetadata<E> resolve(Class<E> entityType);

}
