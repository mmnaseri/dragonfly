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

package com.agileapes.dragonfly.ext;

import com.agileapes.dragonfly.entity.EntityDefinitionInterceptor;
import com.agileapes.dragonfly.metadata.TableMetadataInterceptor;

import java.util.Collection;

/**
 * This interface exposes functionality that will facilitate managing extensions within a data access
 * session. The extension manager is in itself both a metadata interceptor, collectively applying all
 * extension-defined metadata interceptors, and an entity definition interceptor, taking all the
 * definition interceptors introduced through extensions and applying them collectively.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/18, 16:35)
 */
public interface ExtensionManager extends TableMetadataInterceptor, EntityDefinitionInterceptor {

    /**
     * Adds the extension class to the context
     * @param extension    the class describing the extension
     */
    void addExtension(Class<?> extension);

    /**
     * Returns the metadata interceptors based on all extensions
     * @param entityType    the type of entity for which metadata interceptors will be selected
     * @return the metadata interceptors
     */
    Collection<TableMetadataInterceptor> getMetadataInterceptors(Class<?> entityType);

    /**
     * Returns all definition interceptors for the given entity type
     * @param entityType    the entity type for which definition interceptors are selected
     * @return the definition interceptors.
     */
    Collection<EntityDefinitionInterceptor> getDefinitionInterceptors(Class<?> entityType);

}
