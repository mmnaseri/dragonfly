package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.collections.CollectionWrapper;
import com.agileapes.couteau.context.impl.OrderedBeanComparator;
import com.agileapes.couteau.maven.resource.ProjectClassResourceTransformer;
import com.agileapes.couteau.maven.resource.ProjectResourceType;
import com.agileapes.couteau.maven.resource.ProjectResourceTypeFilter;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.couteau.reflection.util.assets.AssignableTypeFilter;
import com.agileapes.couteau.reflection.util.assets.ClassCastingTransformer;
import com.agileapes.couteau.reflection.util.assets.InstantiatingTransformer;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.metadata.impl.AnnotationMetadataResolver;
import com.agileapes.dragonfly.metadata.impl.DefaultMetadataContext;
import com.agileapes.dragonfly.metadata.impl.DefaultMetadataResolverContext;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;
import com.agileapes.dragonfly.statement.impl.StatementRegistryPreparator;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 12:51)
 */
@Component("metadataCollector")
public class MetadataCollectorTask extends PluginTask<PluginExecutor> implements ApplicationContextAware {

    private final MetadataRegistry registry = new DefaultMetadataContext();
    private final StatementRegistry statementRegistry = new StatementRegistry();
    private ApplicationContext applicationContext;

    @Autowired
    private EntityDefinitionContext definitionContext;

    @Override
    protected String getIntro() {
        return "Collecting table metadata ...";
    }

    @Value("#{{defineEntities,findExtensions}}")
    @Override
    public void setDependencies(Collection<PluginTask<PluginExecutor>> dependencies) {
        super.setDependencies(dependencies);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void execute(PluginExecutor pluginExecutor) throws MojoFailureException {
        final CollectionWrapper<Class<?>> classes = with(pluginExecutor.getProjectResources())
                .keep(new ProjectResourceTypeFilter(ProjectResourceType.CLASS))
                .transform(new ProjectClassResourceTransformer());
        final List<TableMetadataInterceptor> interceptors = with(applicationContext.getBeansOfType(TableMetadataInterceptor.class, false, true).values())
                .add(classes.keep(new AssignableTypeFilter(TableMetadataInterceptor.class))
                        .transform(new ClassCastingTransformer<TableMetadataInterceptor>(TableMetadataInterceptor.class))
                        .transform(new InstantiatingTransformer<TableMetadataInterceptor>()).list())
                .sort(new OrderedBeanComparator()).list();
        final MetadataResolver resolver = new AnnotationMetadataResolver();
        final MetadataResolverContext resolverContext = new DefaultMetadataResolverContext(MetadataResolveStrategy.UNAMBIGUOUS, interceptors);
        resolverContext.addMetadataResolver(resolver);
        final StatementRegistryPreparator preparator = new StatementRegistryPreparator(pluginExecutor.getDialect(), resolverContext, registry);
        with(definitionContext.getEntities()).each(new Processor<Class<?>>() {
            @Override
            public void process(Class<?> entityType) {
                preparator.addEntity(entityType);
            }
        });
        preparator.prepare(statementRegistry);
    }

    public MetadataRegistry getMetadataRegistry() {
        return registry;
    }

    public StatementRegistry getStatementRegistry() {
        return statementRegistry;
    }

}
