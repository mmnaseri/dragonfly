package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.couteau.lang.compiler.DynamicClassCompiler;
import com.agileapes.couteau.lang.compiler.impl.DefaultDynamicClassCompiler;
import com.agileapes.couteau.lang.compiler.impl.MappedClassLoader;
import com.agileapes.couteau.lang.compiler.impl.SimpleJavaSourceCompiler;
import com.agileapes.dragonfly.io.OutputManager;
import com.agileapes.dragonfly.model.StatementGenerationModel;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;
import freemarker.template.Template;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 2:07)
 */
public class GenerateStatementRegistryTask extends AbstractCodeGenerationTask{

    public static final String CLASS_NAME = "com.agileapes.dragonfly.statement.GeneratedStatementRegistry";

    @Override
    public void execute(PluginExecutor executor) throws MojoFailureException {
        final MetadataCollectorTask collectorTask = applicationContext.getBean(MetadataCollectorTask.class);
        final StatementRegistry statementRegistry = collectorTask.getStatementRegistry();
        final Collection<String> names = statementRegistry.getBeanNames();
        final StatementGenerationModel model = new StatementGenerationModel();
        for (String name : names) {
            try {
                model.addStatement(name, statementRegistry.get(name));
            } catch (RegistryException ignored) {
            }
        }
        try {
            final Template template = FreemarkerUtils.getConfiguration(getClass(), "/ftl/").getTemplate("statementRegistry.ftl");
            final StringWriter out = new StringWriter();
            template.process(model, out);
            final DynamicClassCompiler compiler = new DefaultDynamicClassCompiler(getClass().getClassLoader());
            compiler.setOption(SimpleJavaSourceCompiler.Option.CLASSPATH, getClassPath(executor));
            compiler.compile(CLASS_NAME, new StringReader(out.toString()));
            final byte[] bytes = ((MappedClassLoader) compiler.getClassLoader()).getBytes(CLASS_NAME);
            final String path = CLASS_NAME.replace('.', File.separatorChar).concat(".class");
            applicationContext.getBean(OutputManager.class).writeOutputFile(path, bytes);
        } catch (Exception e) {
            throw new MojoFailureException("Failed to build statement registry", e);
        }
    }

}
