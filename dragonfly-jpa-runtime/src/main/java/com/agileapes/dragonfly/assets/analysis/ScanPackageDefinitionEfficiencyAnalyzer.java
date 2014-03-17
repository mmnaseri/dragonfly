package com.agileapes.dragonfly.assets.analysis;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.dragonfly.assets.ApplicationDesignAnalyzer;
import com.agileapes.dragonfly.assets.DesignIssue;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
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
                return new DesignIssue(DesignIssue.Severity.IMPORTANT, "scan package: " + descriptor.getKey() + "*",
                        "The package specified is too broad for scanning and this will significantly slow down the" +
                                "process", "Add more base packages, but keep them narrower. Identify and remove " +
                        "packages under which no entities are defined.");
            }
        }).list();
    }

}
