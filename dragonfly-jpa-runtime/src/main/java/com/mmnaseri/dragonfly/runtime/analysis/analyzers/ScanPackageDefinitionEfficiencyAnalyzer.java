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

package com.mmnaseri.dragonfly.runtime.analysis.analyzers;

import com.mmnaseri.couteau.basics.api.Filter;
import com.mmnaseri.couteau.basics.api.Transformer;
import com.mmnaseri.dragonfly.entity.EntityDefinition;
import com.mmnaseri.dragonfly.entity.EntityDefinitionContext;
import com.mmnaseri.dragonfly.runtime.analysis.ApplicationDesignAnalyzer;
import com.mmnaseri.dragonfly.runtime.analysis.DesignIssue;
import com.mmnaseri.dragonfly.runtime.analysis.impl.PackageIssueTarget;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/3/17 AD, 1:49)
 */
public class ScanPackageDefinitionEfficiencyAnalyzer implements ApplicationDesignAnalyzer {

    private final String[] basePackages;
    private final EntityDefinitionContext definitionContext;

    public ScanPackageDefinitionEfficiencyAnalyzer(String[] basePackages, EntityDefinitionContext definitionContext) {
        this.basePackages = basePackages;
        this.definitionContext = definitionContext;
    }

    @Override
    public List<DesignIssue> analyze() {
        //noinspection unchecked
        return with(basePackages).transform(new Transformer<String, String>() {
            @Override
            public String map(String packageName) {
                if (!packageName.endsWith(".")) {
                    packageName += ".";
                }
                return packageName;
            }
        }).transform(new Transformer<String, Map.Entry<String, Integer>>() {
            @Override
            public Map.Entry<String, Integer> map(final String packageName) {
                //noinspection unchecked
                return new AbstractMap.SimpleImmutableEntry<String, Integer>(packageName, with(definitionContext.getDefinitions()).transform(new Transformer<EntityDefinition<?>, Integer>() {
                    @Override
                    public Integer map(EntityDefinition<?> entityDefinition) {
                        String canonicalName = entityDefinition.getEntityType().getCanonicalName();
                        if (!canonicalName.startsWith(packageName)) {
                            return Integer.MAX_VALUE;
                        }
                        canonicalName = canonicalName.substring(packageName.length());
                        return canonicalName.split("\\.").length;
                    }
                }).sort().first());
            }
        }).keep(new Filter<Map.Entry<String, Integer>>() {
            @Override
            public boolean accepts(Map.Entry<String, Integer> descriptor) {
                return descriptor.getValue() > 2;
            }
        }).transform(new Transformer<Map.Entry<String, Integer>, DesignIssue>() {
            @Override
            public DesignIssue map(Map.Entry<String, Integer> descriptor) {
                return new DesignIssue(DesignIssue.Severity.IMPORTANT, new PackageIssueTarget(descriptor.getKey()),
                        "The package specified is too broad for scanning and this will significantly slow down the" +
                                "process", "Add more base packages, but keep them narrower. Identify and remove " +
                        "packages under which no entities are defined.");
            }
        }).list();
    }

}
