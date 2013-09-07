package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.couteau.basics.api.Cache;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.basics.api.impl.ConcurrentCache;
import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.agileapes.couteau.reflection.util.assets.GetterMethodFilter;
import com.agileapes.dragonfly.annotations.Mapping;
import com.agileapes.dragonfly.annotations.Partial;
import com.agileapes.dragonfly.error.PartialEntityDefinitionError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static com.agileapes.couteau.reflection.util.ReflectionUtils.withMethods;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/7, 13:22)
 */
public class ColumnMappingMetadataCollector {

    private final Cache<Class<?>, Collection<ColumnMetadata>> cache = new ConcurrentCache<Class<?>, Collection<ColumnMetadata>>();

    public Collection<ColumnMetadata> collectMetadata(Class<?> partialEntity) {
        if (cache.contains(partialEntity)) {
            return cache.read(partialEntity);
        }
        if (!partialEntity.isAnnotationPresent(Partial.class)) {
            throw new PartialEntityDefinitionError("Expected to find @Partial on " + partialEntity.getCanonicalName());
        }
        //noinspection unchecked
        final List<ColumnMetadata> result = withMethods(partialEntity).keep(new GetterMethodFilter()).keep(new AnnotatedElementFilter(Mapping.class)).transform(new Transformer<Method, ColumnMetadata>() {
            @Override
            public ColumnMetadata map(Method method) {
                final String propertyName = ReflectionUtils.getPropertyName(method.getName());
                final Mapping annotation = method.getAnnotation(Mapping.class);
                return new ResolvedColumnMetadata(null, annotation.column(), 0, propertyName, method.getReturnType(), annotation.optional(), 0, 0, 0);
            }
        }).list();
        cache.write(partialEntity, result);
        return result;
    }

}
