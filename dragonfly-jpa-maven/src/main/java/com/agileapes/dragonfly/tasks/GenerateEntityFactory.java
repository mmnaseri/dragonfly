package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.enhancer.api.NamingPolicy;
import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.couteau.lang.compiler.DynamicClassCompiler;
import com.agileapes.couteau.lang.compiler.impl.DefaultDynamicClassCompiler;
import com.agileapes.couteau.lang.compiler.impl.SimpleJavaSourceCompiler;
import com.agileapes.couteau.maven.resource.ProjectResource;
import com.agileapes.couteau.maven.resource.ProjectResourceType;
import com.agileapes.couteau.reflection.cp.MappedClassLoader;
import com.agileapes.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.agileapes.dragonfly.cg.StaticNamingPolicy;
import com.agileapes.dragonfly.io.OutputManager;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoFailureException;

import javax.persistence.Entity;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/15, 15:35)
 */
public class GenerateEntityFactory extends AbstractCodeGenerationTask {

    @Override
    public void execute(PluginExecutor executor) throws MojoFailureException {
        final DynamicClassCompiler compiler = new DefaultDynamicClassCompiler(executor.getProjectClassLoader());
        try {
            compiler.setOption(SimpleJavaSourceCompiler.Option.CLASSPATH, getClassPath(executor));
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoFailureException("Classpath resolution failed", e);
        }
        final NamingPolicy namingPolicy = new StaticNamingPolicy("entity");
        final Configuration configuration = FreemarkerUtils.getConfiguration(getClass(), "/ftl/");
        final OutputManager outputManager = applicationContext.getBean(OutputManager.class);
        final Template entityTemplate;
        final Template factoryTemplate;
        try {
            entityTemplate = configuration.getTemplate("simpleEntity.ftl");
            factoryTemplate = configuration.getTemplate("entityFactory.ftl");
        } catch (IOException e) {
            throw new MojoFailureException("Failed to locate template", e);
        }
        //noinspection unchecked
        with(executor.getProjectResources()).keep(new Filter<ProjectResource>() {
            @Override
            public boolean accepts(ProjectResource item) {
                return item.getType().equals(ProjectResourceType.CLASS);
            }
        }).transform(new Transformer<ProjectResource, Class<?>>() {
            @Override
            public Class<?> map(ProjectResource input) {
                return input.getClassArtifact();
            }
        }).keep(new AnnotatedElementFilter(Entity.class)).each(new Processor<Class<?>>() {
            @Override
            public void process(Class<?> input) {
                final String className = namingPolicy.getClassName(input, null);
                final HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("name", className);
                map.put("entityType", input);
                StringWriter out = new StringWriter();
                try {
                    entityTemplate.process(map, out);
                } catch (Exception e) {
                    throw new Error("Failed to process template", e);
                }
                byte[] bytes;
                try {
                    compiler.compile(className, new StringReader(out.toString()));
                    bytes = ((MappedClassLoader) compiler.getClassLoader()).getBytes(className);
                } catch (Exception e) {
                    throw new Error("Compilation failed", e);
                }
                out = new StringWriter();
                final String entityPath = className.replace('.', File.separatorChar).concat(".class");
                try {
                    outputManager.writeOutputFile(entityPath, bytes);
                } catch (IOException e) {
                    throw new Error("Failed to write output", e);
                }
                try {
                    factoryTemplate.process(map, out);
                } catch (Exception e) {
                    throw new Error("Failed to process template", e);
                }
                final String factoryName = input.getCanonicalName().concat("EntityFactory");
                try {
                    compiler.compile(factoryName, new StringReader(out.toString()));
                    bytes = ((MappedClassLoader) compiler.getClassLoader()).getBytes(factoryName);
                } catch (Exception e) {
                    throw new Error("Compilation failed", e);
                }
                final String factoryPath = factoryName.replace('.', File.separatorChar).concat(".class");
                try {
                    outputManager.writeOutputFile(factoryPath, bytes);
                } catch (IOException e) {
                    throw new Error("Failed to write output", e);
                }
                outputManager.deleteOutput(entityPath);
            }
        });
    }

}
