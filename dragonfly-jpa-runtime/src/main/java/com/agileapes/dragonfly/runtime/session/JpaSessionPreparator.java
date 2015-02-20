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

package com.agileapes.dragonfly.runtime.session;

import com.agileapes.dragonfly.ext.ExtensionMetadataResolver;
import com.agileapes.dragonfly.ext.impl.AnnotationExtensionMetadataResolver;
import com.agileapes.dragonfly.metadata.TableMetadataResolver;
import com.agileapes.dragonfly.metadata.impl.AnnotationTableMetadataResolver;
import com.agileapes.dragonfly.runtime.session.impl.AbstractSessionPreparator;
import com.agileapes.dragonfly.runtime.session.impl.AnnotatedExtensionDefinitionLookupSource;

import java.util.Arrays;
import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/14 AD, 9:31)
 */
public class JpaSessionPreparator extends AbstractSessionPreparator {

    private AnnotationTableMetadataResolver metadataResolver;

    public JpaSessionPreparator(ClassLoader classLoader) {
        addEntityDefinitionSource(new AnnotatedEntityDefinitionLookupSource(classLoader));
        addExtensionDefinitionSource(new AnnotatedExtensionDefinitionLookupSource(classLoader));
    }

    @Override
    protected List<TableMetadataResolver> getMetadataResolvers() {
        return Arrays.<TableMetadataResolver>asList(getAnnotationTableMetadataResolver());
    }

    @Override
    protected ExtensionMetadataResolver<Class<?>> getExtensionMetadataResolver() {
        return new AnnotationExtensionMetadataResolver(metadataResolver);
    }

    private AnnotationTableMetadataResolver getAnnotationTableMetadataResolver() {
        return metadataResolver = metadataResolver == null ? new AnnotationTableMetadataResolver(getDatabaseDialect()) : metadataResolver;
    }

}
