package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.dragonfly.io.OutputManager;
import com.agileapes.dragonfly.metadata.impl.DefaultMetadataContext;
import com.agileapes.dragonfly.model.ApplicationContextModel;
import com.agileapes.dragonfly.model.BeanDefinitionModel;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import com.agileapes.dragonfly.security.impl.DefaultDataSecurityManager;
import com.agileapes.dragonfly.security.impl.FatalAccessDeniedHandler;
import freemarker.template.Template;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.StringWriter;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 1:13)
 */
public class GenerateApplicationContextTask extends PluginTask<PluginExecutor> implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void execute(PluginExecutor executor) throws MojoFailureException {
        final ApplicationContextModel model = new ApplicationContextModel();
        final BeanDefinitionModel propertyPlaceholder = new BeanDefinitionModel("org.springframework.beans.factory.config.PropertyPlaceholderConfigurer", "org.springframework.beans.factory.config.PropertyPlaceholderConfigurer");
        propertyPlaceholder.setProperty("locations", "classpath:db.properties");
        model.addBean(propertyPlaceholder);
        model.addBean(new BeanDefinitionModel("_generatedMetadataRegistry", GenerateMetadataRegistryTask.CLASS_NAME));
        model.addBean(new BeanDefinitionModel("_databaseDialect", executor.getDialect().getClass().getCanonicalName()));
        final BeanDefinitionModel metadataContext = new BeanDefinitionModel("metadataContext", DefaultMetadataContext.class.getCanonicalName());
        metadataContext.setReference("registries", "_generatedMetadataRegistry");
        model.addBean(metadataContext);
        model.addBean(new BeanDefinitionModel("_accessDeniedHandler", FatalAccessDeniedHandler.class.getCanonicalName()));
        final OutputManager outputManager = applicationContext.getBean(OutputManager.class);
        try {
            final Template template = FreemarkerUtils.getConfiguration(getClass(), "/ftl/").getTemplate("applicationContext.ftl");
            final StringWriter out = new StringWriter();
            template.process(model, out);
            outputManager.writeSourceFile("/src/main/resources/data/setup.xml", out.toString());
        } catch (Exception e) {
            throw new MojoFailureException("Failed to produce application context XML", e);
        }
    }
}
