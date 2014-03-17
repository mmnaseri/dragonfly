package com.agileapes.dragonfly.analysis.analyzers;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.agileapes.couteau.reflection.util.assets.GetterMethodFilter;
import com.agileapes.dragonfly.analysis.ApplicationDesignAnalyzer;
import com.agileapes.dragonfly.analysis.DesignIssue;
import com.agileapes.dragonfly.analysis.impl.MethodIssueTarget;
import com.agileapes.dragonfly.ext.ExtensionManager;
import com.agileapes.dragonfly.ext.ExtensionMetadata;

import javax.persistence.Column;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.agileapes.couteau.reflection.util.ReflectionUtils.withMethods;

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
