/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.maven.resource.ProjectClassResourceTransformer;
import com.agileapes.couteau.maven.resource.ProjectResourceType;
import com.agileapes.couteau.maven.resource.ProjectResourceTypeFilter;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.entity.EntityDefinitionInterceptor;
import com.agileapes.dragonfly.entity.impl.ImmutableEntityDefinition;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import java.util.Collection;
import java.util.HashMap;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 15:13)
 */
@Component("defineEntities")
public class EntityDefinitionCollectorTask extends PluginTask<PluginExecutor> {

    @Autowired
    private EntityDefinitionContext definitionContext;

    @Autowired
    private EntityDefinitionInterceptor extensionDefinitionInterceptor;

    @Override
    protected String getIntro() {
        return "Collecting metadata about entity definitions ...";
    }

    @Value("#{findExtensions}")
    @Override
    public void setDependencies(Collection<PluginTask<PluginExecutor>> dependencies) {
        super.setDependencies(dependencies);
    }

    @Override
    public void execute(PluginExecutor executor) throws MojoFailureException {
        definitionContext.addInterceptor(extensionDefinitionInterceptor);
        //noinspection unchecked
        with(executor.getProjectResources())
        .keep(new ProjectResourceTypeFilter(ProjectResourceType.CLASS))
        .transform(new ProjectClassResourceTransformer())
        .keep(new AnnotatedElementFilter(Entity.class))
        .transform(new Transformer<Class<?>, EntityDefinition<?>>() {
            @Override
            public EntityDefinition<?> map(Class<?> input) {
                //noinspection unchecked
                return new ImmutableEntityDefinition<Object>((Class<Object>) input, new HashMap<Class<?>, Class<?>>());
            }
        }).each(new Processor<EntityDefinition<?>>() {
            @Override
            public void process(EntityDefinition<?> input) {
                definitionContext.addDefinition(input);
            }
        });
    }

}
