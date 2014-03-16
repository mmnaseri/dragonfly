package com.agileapes.dragonfly.assets.analysis;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.agileapes.couteau.reflection.util.assets.GetterMethodFilter;
import com.agileapes.dragonfly.assets.ApplicationDesignAnalyzer;
import com.agileapes.dragonfly.assets.DesignIssue;
import com.agileapes.dragonfly.ext.ExtensionManager;
import com.agileapes.dragonfly.ext.ExtensionMetadata;
import com.agileapes.dragonfly.session.SessionPreparator;
import org.springframework.context.ApplicationContext;

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

    @Override
    public List<DesignIssue> analyze(ApplicationContext applicationContext, SessionPreparator sessionPreparator) {
        final ArrayList<DesignIssue> issues = new ArrayList<DesignIssue>();
        final ExtensionManager extensionManager = sessionPreparator.getExtensionManager();
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
                    return new DesignIssue(DesignIssue.Severity.CRITICAL, "method declaration for '" + method.toGenericString() + "'", "Column method is declared by extension class. This might cause " +
                            "property access problems with data access", "Move the column value setter and getter to " +
                            "an interface which the extension implements.");
                }
            }).list());
        }
        return issues;
    }

}
