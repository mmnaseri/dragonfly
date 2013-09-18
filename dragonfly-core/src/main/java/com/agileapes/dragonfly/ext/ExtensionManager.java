package com.agileapes.dragonfly.ext;

import com.agileapes.dragonfly.entity.EntityDefinitionInterceptor;
import com.agileapes.dragonfly.metadata.TableMetadataInterceptor;

import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/18, 16:35)
 */
public interface ExtensionManager extends TableMetadataInterceptor, EntityDefinitionInterceptor {

    void addExtension(Class<?> extension);

    Collection<TableMetadataInterceptor> getMetadataInterceptors(Class<?> entityType);

    Collection<EntityDefinitionInterceptor> getDefinitionInterceptors(Class<?> entityType);

}
