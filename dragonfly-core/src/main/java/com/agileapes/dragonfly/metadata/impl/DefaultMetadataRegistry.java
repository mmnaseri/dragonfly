package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.context.contract.Registry;
import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.couteau.context.impl.ConcurrentRegistry;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.metadata.TableMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/30, 14:42)
 */
public class DefaultMetadataRegistry implements MetadataRegistry {

    private final Registry<TableMetadata<?>> registry = new ConcurrentRegistry<TableMetadata<?>>();
    private final Transformer<Class<?>, String> mapper = new Transformer<Class<?>, String>() {
        @Override
        public String map(Class<?> aClass) {
            return aClass.getCanonicalName();
        }
    };

    @Override
    public <E> TableMetadata<E> getTableMetadata(Class<E> entityType) {
        final String key = mapper.map(entityType);
        try {
            //noinspection unchecked
            return registry.contains(key) ? (TableMetadata<E>) registry.get(key) : null;
        } catch (RegistryException e) {
            return null;
        }
    }

    @Override
    public <E> void addTableMetadata(Class<E> entityType, TableMetadata<E> tableMetadata) {
        final String key = mapper.map(entityType);
        try {
            registry.register(key, tableMetadata);
        } catch (RegistryException ignored) {
        }
    }

    @Override
    public boolean contains(Class<?> entityType) {
        return registry.contains(mapper.map(entityType));
    }

}
