package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.enhancer.api.NamingPolicy;
import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.couteau.lang.compiler.DynamicClassCompiler;
import com.agileapes.couteau.lang.compiler.impl.DefaultDynamicClassCompiler;
import com.agileapes.couteau.lang.compiler.impl.SimpleJavaSourceCompiler;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.dragonfly.cg.StaticNamingPolicy;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/15, 15:35)
 */
@Component
public class GenerateEntityFactoryTask extends AbstractCodeGenerationTask {

    public static final String ENTITY_FACTORY_SUFFIX = "EntityFactory";
    @Autowired
    private EntityDefinitionContext definitionContext;

    @Override
    protected String getIntro() {
        return "Generating entity factories";
    }

    @Value("#{enhanceEntities}")
    @Override
    public void setDependencies(Collection<PluginTask<PluginExecutor>> dependencies) {
        super.setDependencies(dependencies);
    }

    @Override
    public void execute(PluginExecutor executor) throws MojoFailureException {
        final DynamicClassCompiler compiler = new DefaultDynamicClassCompiler(executor.getProjectClassLoader());
        try {
            compiler.setOption(SimpleJavaSourceCompiler.Option.CLASSPATH, getClassPath(executor));
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoFailureException("Classpath resolution failed", e);
        }
        final NamingPolicy namingPolicy = new StaticNamingPolicy("Entity");
        final Configuration configuration = FreemarkerUtils.getConfiguration(getClass(), "/ftl/");
        final Template factoryTemplate;
        try {
            factoryTemplate = configuration.getTemplate("entityFactory.ftl");
        } catch (IOException e) {
            throw new MojoFailureException("Failed to locate template", e);
        }
        with(definitionContext.getEntities()).each(new Processor<Class<?>>() {
            @Override
            public void process(Class<?> input) {
                final String className = namingPolicy.getClassName(input, null);
                final HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("name", className);
                map.put("entityType", input);
                final StringWriter out = new StringWriter();
                byte[] bytes;
                try {
                    factoryTemplate.process(map, out);
                } catch (Exception e) {
                    throw new Error("Failed to process template", e);
                }
                final String factoryName = input.getCanonicalName().concat(ENTITY_FACTORY_SUFFIX);
                try {
                    compiler.compile(factoryName, new StringReader(out.toString()));
                    bytes = compiler.getClassLoader().getBytes(factoryName);
                } catch (Exception e) {
                    throw new Error("Compilation failed", e);
                }
                final String factoryPath = factoryName.replace('.', File.separatorChar).concat(".class");
                try {
                    getOutputManager().writeOutputFile(factoryPath, bytes);
                } catch (IOException e) {
                    throw new Error("Failed to write output", e);
                }
            }
        });
    }

}
