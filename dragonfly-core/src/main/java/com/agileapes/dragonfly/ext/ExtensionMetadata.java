package com.agileapes.dragonfly.ext;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.entity.EntityDefinitionInterceptor;
import com.agileapes.dragonfly.metadata.TableMetadataInterceptor;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/18, 16:39)
 */
public interface ExtensionMetadata extends Filter<Class<?>> {

    Class<?> getExtension();

    TableMetadataInterceptor getTableMetadataInterceptor();

    EntityDefinitionInterceptor getEntityDefinitionInterceptor();


}
