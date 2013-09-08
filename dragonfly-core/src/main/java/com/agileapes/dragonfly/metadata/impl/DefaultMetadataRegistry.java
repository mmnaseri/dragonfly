package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.context.contract.Registry;
import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.couteau.context.impl.ConcurrentRegistry;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.Collection;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

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
    private Processor<MetadataRegistry> registryProcessor;

    @Override
    public Collection<Class<?>> getEntityTypes() {
        return with(registry.getBeans()).transform(new Transformer<TableMetadata<?>, Class<?>>() {
            @Override
            public Class<?> map(TableMetadata<?> tableMetadata) {
                return tableMetadata.getEntityType();
            }
        }).list();
    }

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
    public synchronized <E> void addTableMetadata(TableMetadata<E> tableMetadata) {
        final String key = mapper.map(tableMetadata.getEntityType());
        try {
            registry.register(key, tableMetadata);
        } catch (RegistryException ignored) {
        }
        if (registryProcessor != null) {
            registryProcessor.process(this);
        }
    }

    @Override
    public synchronized boolean contains(Class<?> entityType) {
        return registry.contains(mapper.map(entityType));
    }

    @Override
    public void setChangeCallback(Processor<MetadataRegistry> registryProcessor) {
        this.registryProcessor = registryProcessor;
    }

}
