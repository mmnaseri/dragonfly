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

package com.agileapes.dragonfly.runtime.analysis.analyzers;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.ext.ExtensionManager;
import com.agileapes.dragonfly.ext.ExtensionMetadata;
import com.agileapes.dragonfly.runtime.analysis.ApplicationDesignAnalyzer;
import com.agileapes.dragonfly.runtime.analysis.DesignIssue;
import com.agileapes.dragonfly.runtime.analysis.impl.ExtensionIssueTarget;

import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 1:50)
 */
public class ExtensionFilterApplicabilityAnalyzer implements ApplicationDesignAnalyzer {

    private final ExtensionManager extensionManager;
    private final EntityDefinitionContext definitionContext;

    public ExtensionFilterApplicabilityAnalyzer(ExtensionManager extensionManager, EntityDefinitionContext definitionContext) {
        this.extensionManager = extensionManager;
        this.definitionContext = definitionContext;
    }

    @Override
    public List<DesignIssue> analyze() {
        //noinspection unchecked
        return with(extensionManager.getRegisteredExtensions())
                .keep(new Filter<ExtensionMetadata>() {
                    @Override
                    public boolean accepts(ExtensionMetadata extensionMetadata) {
                        return with(definitionContext.getDefinitions())
                                .transform(new Transformer<EntityDefinition<?>, Class<?>>() {
                                    @Override
                                    public Class<?> map(EntityDefinition<?> entityDefinition) {
                                        return entityDefinition.getEntityType();
                                    }
                                })
                                .keep(extensionMetadata)
                                .isEmpty();
                    }
                })
                .transform(new Transformer<ExtensionMetadata, DesignIssue>() {
                    @Override
                    public DesignIssue map(ExtensionMetadata extensionMetadata) {
                        return new DesignIssue(DesignIssue.Severity.IMPORTANT, new ExtensionIssueTarget(extensionMetadata),
                                "Extension does not apply to any of the defined entities. This might indicate that the filter for the " +
                                        "extension has not been written properly", "Inspect the indicated extension's filter and make sure " +
                                "that it has been written and functions as intended, and the fact that it does not extend any entities " +
                                "is by design and not by accident.");
                    }
                }).list();
    }
}
