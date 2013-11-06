/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.couteau.basics.api.Cache;
import com.agileapes.couteau.basics.api.impl.ConcurrentCache;
import com.agileapes.couteau.basics.api.impl.MirrorFilter;
import com.agileapes.couteau.context.impl.OrderedBeanComparator;
import com.agileapes.dragonfly.error.EntityDefinitionError;
import com.agileapes.dragonfly.metadata.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 19:41)
 */
public class DefaultMetadataResolverContext implements MetadataResolverContext {

    private final Cache<Class<?>, TableMetadata<?>> cache = new ConcurrentCache<Class<?>, TableMetadata<?>>();
    private final List<MetadataResolver> resolvers = new CopyOnWriteArrayList<MetadataResolver>();
    private final MetadataResolveStrategy resolveStrategy;
    private final List<TableMetadataInterceptor> interceptors;

    public DefaultMetadataResolverContext() {
        this(Collections.<TableMetadataInterceptor>emptyList());
    }

    public DefaultMetadataResolverContext(List<TableMetadataInterceptor> interceptors) {
        this(MetadataResolveStrategy.UNAMBIGUOUS, interceptors);
    }

    public DefaultMetadataResolverContext(MetadataResolveStrategy resolveStrategy) {
        this(resolveStrategy, Collections.<TableMetadataInterceptor>emptyList());
    }

    public DefaultMetadataResolverContext(MetadataResolveStrategy resolveStrategy, List<TableMetadataInterceptor> interceptors) {
        this.resolveStrategy = resolveStrategy;
        this.interceptors = with(interceptors).sort(new OrderedBeanComparator()).list();
    }

    @Override
    public void addMetadataResolver(MetadataResolver metadataResolver) {
        resolvers.add(new AugmentedMetadataResolver(metadataResolver, interceptors));
    }

    @Override
    public <E> TableMetadata<E> resolve(Class<E> entityType) {
        if (cache.contains(entityType)) {
            //noinspection unchecked
            final TableMetadata<E> tableMetadata = (TableMetadata<E>) cache.read(entityType);
            if (tableMetadata == null) {
                throw new EntityDefinitionError("Failed to resolve metadata for this entity:" + entityType.getCanonicalName());
            }
            return tableMetadata;
        }
        //noinspection unchecked
        final List<MetadataResolver> list = with(resolvers).keep(new MirrorFilter<Class<?>>(entityType)).list();
        if (list.size() == 0) {
            throw new EntityDefinitionError("No metadata resolvers could be found for the given entity: " + entityType.getCanonicalName());
        }
        final TableMetadata<E> tableMetadata;
        if (list.size() == 1) {
            tableMetadata = list.get(0).resolve(entityType);
        } else {
            if (MetadataResolveStrategy.UNAMBIGUOUS.equals(resolveStrategy)) {
                cache.write(entityType, null);
                throw new EntityDefinitionError("There are more than one way to resolve metadata for this entity:" + entityType.getCanonicalName());
            } else {
                tableMetadata = list.get(0).resolve(entityType);
            }
        }
        cache.write(entityType, tableMetadata);
        return tableMetadata;
    }

    @Override
    public boolean accepts(Class<?> entityType) {
        //noinspection unchecked
        return !with(resolvers).keep(new MirrorFilter<Class<?>>(entityType)).isEmpty();
    }

}
