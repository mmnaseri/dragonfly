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

import com.mmnaseri.couteau.basics.api.Filter;
import com.mmnaseri.couteau.basics.api.Transformer;
import com.mmnaseri.couteau.reflection.util.ReflectionUtils;
import com.mmnaseri.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.mmnaseri.couteau.reflection.util.assets.GetterMethodFilter;
import com.agileapes.dragonfly.ext.ExtensionManager;
import com.agileapes.dragonfly.ext.ExtensionMetadata;
import com.agileapes.dragonfly.runtime.analysis.ApplicationDesignAnalyzer;
import com.agileapes.dragonfly.runtime.analysis.DesignIssue;
import com.agileapes.dragonfly.runtime.analysis.impl.MethodIssueTarget;

import javax.persistence.Column;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.mmnaseri.couteau.reflection.util.ReflectionUtils.withMethods;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 1:47)
 */
public class ExtensionPropertyAccessibilityAnalyzer implements ApplicationDesignAnalyzer {

    private final ExtensionManager extensionManager;

    public ExtensionPropertyAccessibilityAnalyzer(ExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }

    @Override
    public List<DesignIssue> analyze() {
        final ArrayList<DesignIssue> issues = new ArrayList<DesignIssue>();
        for (ExtensionMetadata extensionMetadata : extensionManager.getRegisteredExtensions()) {
            final Class<?> extension = extensionMetadata.getExtension();
            //noinspection unchecked
            issues.addAll(withMethods(extension).keep(new GetterMethodFilter()).keep(new AnnotatedElementFilter(Column.class)).keep(new Filter<Method>() {
                @Override
                public boolean accepts(Method method) {
                    return ReflectionUtils.getDeclaringClass(method).equals(extension);
                }
            }).transform(new Transformer<Method, DesignIssue>() {
                @Override
                public DesignIssue map(Method method) {
                    return new DesignIssue(DesignIssue.Severity.CRITICAL, new MethodIssueTarget(method), "Column method is declared by extension class. This might cause " +
                            "property access problems with data access", "Move the column value setter and getter to " +
                            "an interface which the extension implements.");
                }
            }).list());
        }
        return issues;
    }

}
