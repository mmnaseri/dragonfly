/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mmnaseri.dragonfly.metadata.impl;

import com.mmnaseri.couteau.basics.api.Cache;
import com.mmnaseri.couteau.basics.api.impl.ConcurrentCache;
import com.mmnaseri.couteau.basics.api.impl.MirrorFilter;
import com.mmnaseri.couteau.context.impl.OrderedBeanComparator;
import com.mmnaseri.dragonfly.error.EntityDefinitionError;
import com.mmnaseri.dragonfly.metadata.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * This is a simple table metadata resolver, with the difference that it will take in
 * multiple metadata resolvers and decide whether or not it can resolve metadata for a
 * given entity type.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/8, 19:41)
 */
public class DefaultTableMetadataResolverContext implements TableMetadataResolverContext {

    private final Cache<Class<?>, TableMetadata<?>> cache = new ConcurrentCache<Class<?>, TableMetadata<?>>();
    private final List<TableMetadataResolver> resolvers = new CopyOnWriteArrayList<TableMetadataResolver>();
    private final MetadataResolveStrategy resolveStrategy;
    private final List<TableMetadataInterceptor> interceptors;

    public DefaultTableMetadataResolverContext() {
        this(Collections.<TableMetadataInterceptor>emptyList());
    }

    public DefaultTableMetadataResolverContext(List<TableMetadataInterceptor> interceptors) {
        this(MetadataResolveStrategy.UNAMBIGUOUS, interceptors);
    }

    public DefaultTableMetadataResolverContext(MetadataResolveStrategy resolveStrategy) {
        this(resolveStrategy, Collections.<TableMetadataInterceptor>emptyList());
    }

    public DefaultTableMetadataResolverContext(MetadataResolveStrategy resolveStrategy, List<TableMetadataInterceptor> interceptors) {
        this.resolveStrategy = resolveStrategy;
        this.interceptors = with(interceptors).sort(new OrderedBeanComparator()).list();
    }

    @Override
    public void addMetadataResolver(TableMetadataResolver tableMetadataResolver) {
        resolvers.add(new AugmentedTableMetadataResolver(tableMetadataResolver, interceptors));
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
        final List<TableMetadataResolver> list = with(resolvers).keep(new MirrorFilter<Class<?>>(entityType)).list();
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
