package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.couteau.lang.compiler.DynamicClassCompiler;
import com.agileapes.couteau.lang.compiler.impl.DefaultDynamicClassCompiler;
import com.agileapes.couteau.lang.compiler.impl.SimpleJavaSourceCompiler;
import com.agileapes.couteau.lang.error.CompileException;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.couteau.reflection.cp.MappedClassLoader;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.model.MetadataGenerationModel;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 12:50)
 */
@Component
public class GenerateMetadataRegistryTask extends AbstractCodeGenerationTask {

    public static final String CLASS_NAME = "com.agileapes.dragonfly.metadata.GeneratedJpaMetadataRegistry";

    @Value("#{metadataCollector}")
    @Override
    public void setDependencies(Collection<PluginTask<PluginExecutor>> dependencies) {
        super.setDependencies(dependencies);
    }

    @Value("#{metadataCollector.metadataRegistry}")
    private MetadataRegistry metadataRegistry;

    @Override
    public void execute(PluginExecutor pluginExecutor) throws MojoFailureException {
        final Collection<Class<?>> entityTypes = metadataRegistry.getEntityTypes();
        final Configuration configuration = FreemarkerUtils.getConfiguration(GenerateMetadataRegistryTask.class, "/ftl");
        final HashSet<TableMetadata<?>> tables = new HashSet<TableMetadata<?>>();
        final MetadataGenerationModel model = new MetadataGenerationModel();
        model.setTables(tables);
        for (Class<?> entityType : entityTypes) {
            tables.add(metadataRegistry.getTableMetadata(entityType));
        }
        final Template template;
        try {
            template = configuration.getTemplate("metadataRegistry.ftl");
        } catch (IOException e) {
            throw new MojoFailureException("Failed to load template", e);
        }
        final StringWriter out = new StringWriter();
        try {
            template.process(model, out);
        } catch (TemplateException e) {
            throw new MojoFailureException("There was an error processing the template", e);
        } catch (IOException e) {
            throw new MojoFailureException("There was an I/O error in template processing", e);
        }
        DynamicClassCompiler compiler = new DefaultDynamicClassCompiler(getClass().getClassLoader());
        try {
            compiler.setOption(SimpleJavaSourceCompiler.Option.CLASSPATH, getClassPath(pluginExecutor));
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoFailureException("Failed to resolve dependencies", e);
        }
        try {
            compiler.compile(CLASS_NAME, new StringReader(out.toString()));
        } catch (CompileException e) {
            throw new MojoFailureException("Compilation error in metadata registry", e);
        }
        final byte[] bytes;
        try {
            bytes = ((MappedClassLoader) compiler.getClassLoader()).getBytes(CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new MojoFailureException("No such class: " + CLASS_NAME, e);
        }
        final String path = CLASS_NAME.replace('.', File.separatorChar).concat(".class");
        try {
            getOutputManager().writeOutputFile(path, bytes);
        } catch (IOException e) {
            throw new MojoFailureException("Failed to write to the output", e);
        }
    }

}
