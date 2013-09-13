package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.couteau.maven.resource.ProjectResource;
import com.agileapes.couteau.maven.resource.ProjectResourceType;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.dragonfly.events.DataAccessPostProcessor;
import com.agileapes.dragonfly.io.OutputManager;
import com.agileapes.dragonfly.metadata.impl.DefaultMetadataContext;
import com.agileapes.dragonfly.model.ApplicationContextModel;
import com.agileapes.dragonfly.model.BeanDefinitionModel;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import com.agileapes.dragonfly.security.impl.FatalAccessDeniedHandler;
import freemarker.template.Template;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.StringWriter;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

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
        //noinspection unchecked
        with(executor.getProjectResources()).keep(new Filter<ProjectResource>() {
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
                return DataAccessPostProcessor.class.isAssignableFrom(item);
            }
        }).each(new Processor<Class<?>>() {
            @Override
            public void process(Class<?> input) {
                model.addBean(new BeanDefinitionModel("_postProcessor_" + input.getSimpleName(), input.getCanonicalName()));
            }
        });
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
