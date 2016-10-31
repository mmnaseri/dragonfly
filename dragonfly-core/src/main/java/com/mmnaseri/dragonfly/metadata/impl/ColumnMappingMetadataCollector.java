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
import com.mmnaseri.couteau.basics.api.Transformer;
import com.mmnaseri.couteau.basics.api.impl.ConcurrentCache;
import com.mmnaseri.couteau.reflection.util.ReflectionUtils;
import com.mmnaseri.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.mmnaseri.couteau.reflection.util.assets.GetterMethodFilter;
import com.mmnaseri.dragonfly.annotations.MappedColumn;
import com.mmnaseri.dragonfly.annotations.Partial;
import com.mmnaseri.dragonfly.error.PartialEntityDefinitionError;
import com.mmnaseri.dragonfly.metadata.ColumnMetadata;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static com.mmnaseri.couteau.reflection.util.ReflectionUtils.withMethods;

/**
 * This is a class that will resolve column metadata for a given partial entity.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/7, 13:22)
 * @see Partial
 * @see MappedColumn
 */
public class ColumnMappingMetadataCollector {

    private final Cache<Class<?>, Collection<ColumnMetadata>> cache = new ConcurrentCache<Class<?>, Collection<ColumnMetadata>>();

    public Collection<ColumnMetadata> collectMetadata(final Class<?> partialEntity) {
        if (cache.contains(partialEntity)) {
            return cache.read(partialEntity);
        }
        if (!partialEntity.isAnnotationPresent(Partial.class)) {
            throw new PartialEntityDefinitionError("Expected to find @Partial on " + partialEntity.getCanonicalName());
        }
        //noinspection unchecked
        final List<ColumnMetadata> result = withMethods(partialEntity).keep(new GetterMethodFilter()).keep(new AnnotatedElementFilter(MappedColumn.class)).transform(new Transformer<Method, ColumnMetadata>() {
            @Override
            public ColumnMetadata map(Method method) {
                final String propertyName = ReflectionUtils.getPropertyName(method.getName());
                final MappedColumn annotation = method.getAnnotation(MappedColumn.class);
                return new ResolvedColumnMetadata(null, partialEntity, annotation.column().isEmpty() ? propertyName : annotation.column(), 0, propertyName, method.getReturnType(), annotation.optional(), 0, 0, 0, false, false);
            }
        }).list();
        cache.write(partialEntity, result);
        return result;
    }

}
