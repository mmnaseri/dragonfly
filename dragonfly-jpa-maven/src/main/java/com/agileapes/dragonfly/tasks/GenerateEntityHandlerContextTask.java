package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.entity.impl.HandlerContextPreparatorPostProcessor;
import com.agileapes.dragonfly.io.OutputManager;
import com.agileapes.dragonfly.model.ApplicationContextModel;
import com.agileapes.dragonfly.model.BeanDefinitionModel;
import com.agileapes.dragonfly.model.BeanPropertyModel;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/18, 9:39)
 */
@Component
public class GenerateEntityHandlerContextTask extends PluginTask<PluginExecutor> {

    @Autowired
    private EntityDefinitionContext definitionContext;

    @Autowired
    private OutputManager outputManager;

    @Value("#{defineEntities}")
    @Override
    public void setDependencies(Collection<PluginTask<PluginExecutor>> dependencies) {
        super.setDependencies(dependencies);
    }

    @Override
    protected String getIntro() {
        return "Generating entity handlers context";
    }

    @Override
    public void execute(PluginExecutor executor) throws MojoFailureException {
        final Configuration configuration = FreemarkerUtils.getConfiguration(getClass(), "/ftl/");
        final Template handlersTemplate;
        try {
            handlersTemplate = configuration.getTemplate("applicationContext.ftl");
        } catch (IOException e) {
            throw new MojoFailureException("Failed to locate template", e);
        }
        final StringWriter out = new StringWriter();
        try {
            final ApplicationContextModel model = new ApplicationContextModel();
            final BeanDefinitionModel context = new BeanDefinitionModel(HandlerContextPreparatorPostProcessor.class.getCanonicalName());
            model.addBean(context);
            final HashSet<BeanDefinitionModel> handlers = new HashSet<BeanDefinitionModel>();
            context.setProperty(new BeanPropertyModel("handlers", handlers));
            for (Class<?> entityType : definitionContext.getEntities()) {
                handlers.add(new BeanDefinitionModel(entityType.getCanonicalName().concat(GenerateEntityHandlersTask.ENTITY_HANDLER_SUFFIX)));
            }
            handlersTemplate.process(model, out);
        } catch (TemplateException e) {
            e.printStackTrace();
            throw new MojoFailureException("Failed to process template", e);
        } catch (IOException e) {
            throw new MojoFailureException("Failed to produce output", e);
        }
        try {
            outputManager.writeSourceFile("/src/main/resources/data/handlers.xml", out.toString());
        } catch (IOException e) {
            throw new MojoFailureException("Failed to write output file");
        }
    }

}
