/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
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

package com.agileapes.dragonfly.ext.impl;

import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.basics.api.impl.MirrorFilter;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionInterceptor;
import com.agileapes.dragonfly.ext.ExtensionManager;
import com.agileapes.dragonfly.ext.ExtensionMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.TableMetadataInterceptor;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * This is the default extension manager used throughout the application
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/18, 16:37)
 */
public class DefaultExtensionManager implements ExtensionManager {

    private final Set<ExtensionMetadata> extensions = new CopyOnWriteArraySet<ExtensionMetadata>();

    @Override
    public void addExtension(ExtensionMetadata extensionMetadata) {
        extensions.add(extensionMetadata);
    }

    @Override
    public Collection<ExtensionMetadata> getRegisteredExtensions() {
        return Collections.unmodifiableCollection(extensions);
    }

    @Override
    public Collection<TableMetadataInterceptor> getMetadataInterceptors(Class<?> entityType) {
        //noinspection unchecked
        return with(extensions).keep(new MirrorFilter<Class<?>>(entityType)).transform(new Transformer<ExtensionMetadata, TableMetadataInterceptor>() {
            @Override
            public TableMetadataInterceptor map(ExtensionMetadata input) {
                return input.getTableMetadataInterceptor();
            }
        }).list();
    }

    @Override
    public Collection<EntityDefinitionInterceptor> getDefinitionInterceptors(Class<?> entityType) {
        //noinspection unchecked
        return with(extensions).keep(new MirrorFilter<Class<?>>(entityType)).transform(new Transformer<ExtensionMetadata, EntityDefinitionInterceptor>() {
            @Override
            public EntityDefinitionInterceptor map(ExtensionMetadata input) {
                return input.getEntityDefinitionInterceptor();
            }
        }).list();
    }

    @Override
    public <E> EntityDefinition<E> intercept(EntityDefinition<E> definition) {
        EntityDefinition<E> entityDefinition = definition;
        final Collection<EntityDefinitionInterceptor> interceptors = getDefinitionInterceptors(entityDefinition.getEntityType());
        for (EntityDefinitionInterceptor interceptor : interceptors) {
            entityDefinition = interceptor.intercept(entityDefinition);
        }
        return entityDefinition;
    }

    @Override
    public <E> TableMetadata<E> intercept(TableMetadata<E> tableMetadata) {
        TableMetadata<E> metadata = tableMetadata;
        final Collection<TableMetadataInterceptor> interceptors = getMetadataInterceptors(metadata.getEntityType());
        for (TableMetadataInterceptor interceptor : interceptors) {
            metadata = interceptor.intercept(metadata);
        }
        return metadata;
    }

}
