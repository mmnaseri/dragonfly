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

package com.mmnaseri.dragonfly.ext;

import com.mmnaseri.dragonfly.entity.EntityDefinitionInterceptor;
import com.mmnaseri.dragonfly.metadata.TableMetadataInterceptor;

import java.util.Collection;

/**
 * This interface exposes functionality that will facilitate managing extensions within a data access
 * session. The extension manager is in itself both a metadata interceptor, collectively applying all
 * extension-defined metadata interceptors, and an entity definition interceptor, taking all the
 * definition interceptors introduced through extensions and applying them collectively.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/18, 16:35)
 */
public interface ExtensionManager extends TableMetadataInterceptor, EntityDefinitionInterceptor {

    /**
     * Adds the extension class to the context
     * @param extension    the metadata describing the extension
     */
    void addExtension(ExtensionMetadata extension);

    /**
     * @return a collection of all registered extensions
     */
    Collection<ExtensionMetadata> getRegisteredExtensions();

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
