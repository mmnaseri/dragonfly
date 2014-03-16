package com.agileapes.dragonfly.assets.analysis;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.basics.api.impl.MirrorFilter;
import com.agileapes.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.agileapes.couteau.reflection.util.assets.GetterMethodFilter;
import com.agileapes.dragonfly.assets.ApplicationDesignAnalyzer;
import com.agileapes.dragonfly.assets.ComplexDesignIssueTarget;
import com.agileapes.dragonfly.assets.DesignIssue;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.ext.ExtensionManager;
import com.agileapes.dragonfly.ext.ExtensionMetadata;
import com.agileapes.dragonfly.session.SessionPreparator;
import org.springframework.context.ApplicationContext;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;
import static com.agileapes.couteau.reflection.util.ReflectionUtils.withMethods;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 2:08)
 */
public class IdentityCollisionAnalyzer implements ApplicationDesignAnalyzer {

    @Override
    @SuppressWarnings("unchecked")
    public List<DesignIssue> analyze(ApplicationContext applicationContext, SessionPreparator sessionPreparator) {
        final EntityDefinitionContext definitionContext = sessionPreparator.getDefinitionContext();
        final ExtensionManager extensionManager = sessionPreparator.getExtensionManager();
        with(definitionContext.getDefinitions())
                .transform(new Transformer<EntityDefinition<?>, Class<?>>() {
                    @Override
                    public Class<?> map(EntityDefinition<?> entityDefinition) {
                        return entityDefinition.getEntityType();
                    }
                })
        .keep(new Filter<Class<?>>() {
            @Override
            public boolean accepts(Class<?> entityType) {
                return !withMethods(entityType).keep(new GetterMethodFilter()).keep(new AnnotatedElementFilter(Id.class)).isEmpty();
            }
        })
        .transform(new Transformer<Class<?>, ComplexDesignIssueTarget>() {
            @Override
            public ComplexDesignIssueTarget map(Class<?> entityType) {
                final List<String> extensionsTryingToIntroduceId = with(extensionManager.getRegisteredExtensions())
                        .keep(new MirrorFilter<Class<?>>(entityType))
                        .keep(new Filter<ExtensionMetadata>() {
                            @Override
                            public boolean accepts(ExtensionMetadata extensionMetadata) {
                                return !withMethods(extensionMetadata.getExtension()).keep(new GetterMethodFilter()).keep(new AnnotatedElementFilter(Id.class)).isEmpty();
                            }
                        })
                        .transform(new Transformer<ExtensionMetadata, String>() {
                            @Override
                            public String map(ExtensionMetadata extensionMetadata) {
                                return "extension '" + extensionMetadata.getExtension().getCanonicalName() + "'";
                            }
                        }).list();
                final ArrayList<Object> involvedParties = new ArrayList<Object>(extensionsTryingToIntroduceId);
                involvedParties.add(0, "entity '" + entityType.getCanonicalName() + "'");
                return new ComplexDesignIssueTarget(involvedParties);
            }
        })
        .transform(new Transformer<ComplexDesignIssueTarget, DesignIssue>() {
            @Override
            public DesignIssue map(ComplexDesignIssueTarget complexDesignIssueTarget) {
                return new DesignIssue(DesignIssue.Severity.SEVERE, complexDesignIssueTarget, "Multiple sources exist " +
                        "for entity ID for the given target", "Try to externalize the ID column for the entity if it " +
                        "defines its own ID column, and also take a closer look at your extensions to make sure they " +
                        "do not apply the same sort of semantic modification to your entity, rendering them useless.");
            }
        });
        return null;
    }

}
