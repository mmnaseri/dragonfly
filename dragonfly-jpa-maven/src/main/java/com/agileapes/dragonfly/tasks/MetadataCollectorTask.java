package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.maven.resource.ProjectResource;
import com.agileapes.couteau.maven.resource.ProjectResourceType;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.metadata.MetadataResolver;
import com.agileapes.dragonfly.metadata.impl.AnnotationMetadataResolver;
import com.agileapes.dragonfly.metadata.impl.DefaultMetadataContext;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;
import com.agileapes.dragonfly.statement.impl.StatementRegistryPreparator;
import org.apache.maven.plugin.MojoFailureException;

import javax.persistence.Entity;
import java.util.Collection;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 12:51)
 */
public class MetadataCollectorTask extends PluginTask<PluginExecutor> {

    private final MetadataRegistry registry = new DefaultMetadataContext();
    private final StatementRegistry statementRegistry = new StatementRegistry();

    @Override
    public void execute(PluginExecutor pluginExecutor) throws MojoFailureException {
        final MetadataResolver resolver = new AnnotationMetadataResolver();
        final Collection<ProjectResource> resources = pluginExecutor.getProjectResources();
        final StatementRegistryPreparator preparator = new StatementRegistryPreparator(pluginExecutor.getDialect(), resolver, registry);
        //noinspection unchecked
        with(resources).keep(new Filter<ProjectResource>() {
            @Override
            public boolean accepts(ProjectResource item) {
                return item.getType().equals(ProjectResourceType.CLASS);
            }
        }).transform(new Transformer<ProjectResource, Class<?>>() {
            @Override
            public Class<?> map(ProjectResource input) {
                return input.getClassArtifact();
            }
        }).keep(new Filter<Class<?>>() {
            @Override
            public boolean accepts(Class<?> item) {
                return item.isAnnotationPresent(Entity.class);
            }
        }).each(new Processor<Class<?>>() {
            @Override
            public void process(Class<?> entityType) {
                preparator.addEntity(entityType);
            }
        });
        preparator.prepare(statementRegistry);
    }

    public MetadataRegistry getRegistry() {
        return registry;
    }

    public StatementRegistry getStatementRegistry() {
        return statementRegistry;
    }
}
