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

package com.agileapes.dragonfly.runtime.analysis;

import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.ext.ExtensionManager;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;
import com.agileapes.dragonfly.runtime.analysis.analyzers.*;
import com.agileapes.dragonfly.runtime.session.SessionPreparator;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 0:40)
 */
@SuppressWarnings("unchecked")
public final class JpaApplicationDesignAdvisor extends ApplicationDesignAdvisor {

    @Override
    protected Collection<ApplicationDesignAnalyzer> getAnalyzers(SessionPreparator sessionPreparator) {
        final String[] basePackages = sessionPreparator.getBasePackages();
        final EntityDefinitionContext definitionContext = sessionPreparator.getEntityDefinitionContext();
        final ExtensionManager extensionManager = sessionPreparator.getExtensionManager();
        final TableMetadataRegistry metadataRegistry = sessionPreparator.getTableMetadataRegistry();
        return Arrays.asList(new ExtensionFilterApplicabilityAnalyzer(extensionManager, definitionContext),
                new ExtensionPropertyAccessibilityAnalyzer(extensionManager),
                new ScanPackageDefinitionEfficiencyAnalyzer(basePackages, definitionContext),
                new IdentityCollisionAnalyzer(extensionManager, definitionContext),
                new VersionEntityWithoutKeyAnalyzer(metadataRegistry),
                new PrimitiveColumnTypeAnalyzer(metadataRegistry)
        );
    }
}
