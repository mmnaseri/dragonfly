package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.maven.resource.ProjectResource;
import com.agileapes.couteau.maven.resource.ProjectResourceType;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.metadata.impl.AnnotationMetadataResolver;
import com.agileapes.dragonfly.metadata.impl.DefaultMetadataContext;
import com.agileapes.dragonfly.metadata.impl.DefaultMetadataResolverContext;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;
import com.agileapes.dragonfly.statement.impl.StatementRegistryPreparator;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collection;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 12:51)
 */
public class MetadataCollectorTask extends PluginTask<PluginExecutor> implements ApplicationContextAware {

    private final MetadataRegistry registry = new DefaultMetadataContext();
    private final StatementRegistry statementRegistry = new StatementRegistry();
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void execute(PluginExecutor pluginExecutor) throws MojoFailureException {
        final ArrayList<TableMetadataInterceptor> interceptors = new ArrayList<TableMetadataInterceptor>(applicationContext.getBeansOfType(TableMetadataInterceptor.class, false, true).values());
        //noinspection unchecked
        interceptors.addAll(with(pluginExecutor.getProjectResources()).keep(new Filter<ProjectResource>() {
            @Override
            public boolean accepts(ProjectResource item) {
                return ProjectResourceType.CLASS.equals(item.getType());
            }
        }).transform(new Transformer<ProjectResource, Class<?>>() {
            @Override
            public Class<?> map(ProjectResource input) {
                return input.getClassArtifact();
            }
        }).keep(new Filter<Class<?>>() {
            @Override
            public boolean accepts(Class<?> item) {
                return TableMetadataInterceptor.class.isAssignableFrom(item);
            }
        }).transform(new Transformer<Class<?>, Class<? extends TableMetadataInterceptor>>() {
            @Override
            public Class<? extends TableMetadataInterceptor> map(Class<?> input) {
                return input.asSubclass(TableMetadataInterceptor.class);
            }
        }).transform(new Transformer<Class<? extends TableMetadataInterceptor>, TableMetadataInterceptor>() {
            @Override
            public TableMetadataInterceptor map(Class<? extends TableMetadataInterceptor> input) {
                try {
                    return input.newInstance();
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }).list());
        final MetadataResolver resolver = new AnnotationMetadataResolver();
        final MetadataResolverContext resolverContext = new DefaultMetadataResolverContext(MetadataResolveStrategy.UNAMBIGUOUS, interceptors);
        resolverContext.addMetadataResolver(resolver);
        final Collection<ProjectResource> resources = pluginExecutor.getProjectResources();
        final StatementRegistryPreparator preparator = new StatementRegistryPreparator(pluginExecutor.getDialect(), resolverContext, registry);
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
