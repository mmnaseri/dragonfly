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

import com.agileapes.couteau.enhancer.api.ClassEnhancer;
import com.agileapes.couteau.enhancer.impl.GeneratingClassEnhancer;
import com.agileapes.couteau.lang.compiler.impl.SimpleJavaSourceCompiler;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.couteau.reflection.cp.MappedClassLoader;
import com.agileapes.dragonfly.cg.StaticNamingPolicy;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.entity.impl.EntityProxy;
import com.agileapes.dragonfly.io.OutputManager;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/17, 22:22)
 */
@Component("enhanceEntities")
public class GeneratedEnhancedEntitiesTask extends AbstractCodeGenerationTask {

    @Autowired
    private EntityDefinitionContext definitionContext;

    @Autowired
    private OutputManager outputManager;

    @Override
    protected String getIntro() {
        return super.getIntro();
    }

    @Value("#{defineEntities}")
    @Override
    public void setDependencies(Collection<PluginTask<PluginExecutor>> dependencies) {
        super.setDependencies(dependencies);
    }

    @Override
    public void execute(PluginExecutor executor) throws MojoFailureException {
        final ClassEnhancer<Object> enhancer = new GeneratingClassEnhancer<Object>(executor.getProjectClassLoader());
        try {
            ((GeneratingClassEnhancer) enhancer).getCompiler().setOption(SimpleJavaSourceCompiler.Option.CLASSPATH, getClassPath(executor));
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoFailureException("Failed to resolve dependencies", e);
        }
        enhancer.setNamingPolicy(new StaticNamingPolicy("Entity"));
        for (EntityDefinition<?> entityDefinition : definitionContext.getDefinitions()) {
            enhancer.setSuperClass(entityDefinition.getEntityType());
            final List<Class<?>> classes = with(entityDefinition.getInterfaces().keySet()).add(EntityProxy.class.getInterfaces()).list();
            enhancer.setInterfaces(classes.toArray(new Class[classes.size()]));
            final Class<?> enhancedClass = enhancer.enhance();
            final MappedClassLoader classLoader = (MappedClassLoader) enhancedClass.getClassLoader();
            for (String className : classLoader.getClassNames()) {
                final byte[] bytes;
                try {
                    bytes = classLoader.getBytes(className);
                } catch (ClassNotFoundException e) {
                    throw new MojoFailureException("Failed to load enhanced class", e);
                }
                final String path = className.replace('.', File.separatorChar).concat(".class");
                try {
                    outputManager.writeOutputFile(path, bytes);
                } catch (IOException e) {
                    throw new MojoFailureException("Failed to write output", e);
                }
            }

        }

    }

}
