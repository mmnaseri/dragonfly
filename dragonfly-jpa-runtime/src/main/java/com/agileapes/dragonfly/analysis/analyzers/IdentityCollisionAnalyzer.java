package com.agileapes.dragonfly.analysis.analyzers;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.basics.api.impl.MirrorFilter;
import com.agileapes.couteau.basics.api.impl.NullFilter;
import com.agileapes.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.agileapes.couteau.reflection.util.assets.GetterMethodFilter;
import com.agileapes.dragonfly.analysis.ApplicationDesignAnalyzer;
import com.agileapes.dragonfly.analysis.IssueTarget;
import com.agileapes.dragonfly.analysis.impl.ComplexDesignIssueTarget;
import com.agileapes.dragonfly.analysis.DesignIssue;
import com.agileapes.dragonfly.analysis.impl.EntityIssueTarget;
import com.agileapes.dragonfly.analysis.impl.ExtensionIssueTarget;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.ext.ExtensionManager;
import com.agileapes.dragonfly.ext.ExtensionMetadata;

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

    private final ExtensionManager extensionManager;
    private final EntityDefinitionContext definitionContext;

    public IdentityCollisionAnalyzer(ExtensionManager extensionManager, EntityDefinitionContext definitionContext) {
        this.extensionManager = extensionManager;
        this.definitionContext = definitionContext;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DesignIssue> analyze() {
        return with(definitionContext.getDefinitions())
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
                final List<IssueTarget<?>> extensionsTryingToIntroduceId = with(extensionManager.getRegisteredExtensions())
                        .keep(new MirrorFilter<Class<?>>(entityType))
                        .keep(new Filter<ExtensionMetadata>() {
                            @Override
                            public boolean accepts(ExtensionMetadata extensionMetadata) {
                                return !withMethods(extensionMetadata.getExtension()).keep(new GetterMethodFilter()).keep(new AnnotatedElementFilter(Id.class)).isEmpty();
                            }
                        })
                        .transform(new Transformer<ExtensionMetadata, IssueTarget<?>>() {
                            @Override
                            public IssueTarget<?> map(ExtensionMetadata extensionMetadata) {
                                return new ExtensionIssueTarget(extensionMetadata);
                            }
                        }).list();
                if (extensionsTryingToIntroduceId.isEmpty()) {
                    return null;
                }
                final ArrayList<IssueTarget<?>> involvedParties = new ArrayList<IssueTarget<?>>(extensionsTryingToIntroduceId);
                involvedParties.add(0, new EntityIssueTarget(entityType));
                return new ComplexDesignIssueTarget(involvedParties);
            }
        })
        .drop(new NullFilter<ComplexDesignIssueTarget>())
        .transform(new Transformer<ComplexDesignIssueTarget, DesignIssue>() {
            @Override
            public DesignIssue map(ComplexDesignIssueTarget complexDesignIssueTarget) {
                return new DesignIssue(DesignIssue.Severity.SEVERE, complexDesignIssueTarget, "Multiple sources exist " +
                        "for entity ID for the given target", "Try to externalize the ID column for the entity if it " +
                        "defines its own ID column, and also take a closer look at your extensions to make sure they " +
                        "do not apply the same sort of semantic modification to your entity, rendering them useless.");
            }
        })
        .list();
    }

}
