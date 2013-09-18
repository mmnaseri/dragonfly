package com.agileapes.dragonfly.ext.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.entity.EntityDefinitionInterceptor;
import com.agileapes.dragonfly.ext.ExtensionMetadata;
import com.agileapes.dragonfly.metadata.TableMetadataInterceptor;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/18, 16:46)
 */
public class ImmutableExtensionMetadata implements ExtensionMetadata {

    private final Class<?> extension;
    private final TableMetadataInterceptor tableMetadataInterceptor;
    private final EntityDefinitionInterceptor entityDefinitionInterceptor;
    private final Filter<Class<?>> filter;

    public ImmutableExtensionMetadata(Class<?> extension, TableMetadataInterceptor tableMetadataInterceptor, EntityDefinitionInterceptor entityDefinitionInterceptor, Filter<Class<?>> filter) {
        this.extension = extension;
        this.tableMetadataInterceptor = tableMetadataInterceptor;
        this.entityDefinitionInterceptor = entityDefinitionInterceptor;
        this.filter = filter;
    }

    @Override
    public Class<?> getExtension() {
        return extension;
    }

    @Override
    public TableMetadataInterceptor getTableMetadataInterceptor() {
        return tableMetadataInterceptor;
    }

    @Override
    public EntityDefinitionInterceptor getEntityDefinitionInterceptor() {
        return entityDefinitionInterceptor;
    }

    @Override
    public boolean accepts(Class<?> item) {
        return filter.accepts(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableExtensionMetadata that = (ImmutableExtensionMetadata) o;
        return extension.equals(that.extension);

    }

    @Override
    public int hashCode() {
        return extension.hashCode();
    }
}
