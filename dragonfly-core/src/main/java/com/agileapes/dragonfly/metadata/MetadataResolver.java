package com.agileapes.dragonfly.metadata;

import com.agileapes.couteau.basics.api.Filter;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 12:55)
 */
public interface MetadataResolver extends Filter<Class<?>> {

    <E> TableMetadata<E> resolve(Class<E> entityType);

}
