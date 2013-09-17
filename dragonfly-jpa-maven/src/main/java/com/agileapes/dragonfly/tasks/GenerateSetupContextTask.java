package com.agileapes.dragonfly.tasks;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.couteau.maven.resource.ProjectResource;
import com.agileapes.couteau.maven.resource.ProjectResourceType;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.dragonfly.data.DataAccessPostProcessor;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.data.impl.SecuredDataAccess;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.io.OutputManager;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.metadata.impl.DefaultMetadataContext;
import com.agileapes.dragonfly.model.ApplicationContextModel;
import com.agileapes.dragonfly.model.BeanDefinitionModel;
import com.agileapes.dragonfly.model.BeanPropertyModel;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import com.agileapes.dragonfly.security.AccessDeniedHandler;
import com.agileapes.dragonfly.security.DataSecurityManager;
import com.agileapes.dragonfly.security.impl.DefaultDataSecurityManager;
import com.agileapes.dragonfly.security.impl.FatalAccessDeniedHandler;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;
import freemarker.template.Template;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 1:13)
 */
@Component
public class GenerateSetupContextTask extends PluginTask<PluginExecutor> {

    @Autowired
    private OutputManager outputManager;

    @Override
    protected String getIntro() {
        return "Generating setup application context";
    }

    @Override
    public void execute(PluginExecutor executor) throws MojoFailureException {
        final ApplicationContextModel model = new ApplicationContextModel();

        final BeanDefinitionModel propertyPlaceholder = new BeanDefinitionModel(PropertyPlaceholderConfigurer.class.getCanonicalName());
        propertyPlaceholder.setProperty(new BeanPropertyModel("locations", "classpath:db.properties"));

        final BeanDefinitionModel metadataRegistry = new BeanDefinitionModel("_generatedMetadataRegistry", GenerateMetadataRegistryTask.CLASS_NAME);

        final BeanDefinitionModel databaseDialect = new BeanDefinitionModel("_databaseDialect", executor.getDialect().getClass().getCanonicalName());

        final BeanDefinitionModel metadataContext = new BeanDefinitionModel("metadataContext", DefaultMetadataContext.class.getCanonicalName());
        metadataContext.setProperty(new BeanPropertyModel("registries", metadataRegistry));

        final BeanDefinitionModel accessDeniedHandler = new BeanDefinitionModel("_accessDeniedHandler", FatalAccessDeniedHandler.class.getCanonicalName());

        final BeanDefinitionModel dataSecurityManager = new BeanDefinitionModel("_dataSecurityManager", DefaultDataSecurityManager.class.getCanonicalName());
        dataSecurityManager.addConstructorArgument(new BeanPropertyModel("", AccessDeniedHandler.class.getCanonicalName(), accessDeniedHandler));

        final BeanDefinitionModel statementRegistry = new BeanDefinitionModel("_generatedStatementRegistry", "com.agileapes.dragonfly.statement.GeneratedStatementRegistry");
        statementRegistry.addConstructorArgument(new BeanPropertyModel("", DatabaseDialect.class.getCanonicalName(), databaseDialect));
        statementRegistry.addConstructorArgument(new BeanPropertyModel("", MetadataRegistry.class.getCanonicalName(), metadataContext));

        final BeanDefinitionModel dataAccessSession = new BeanDefinitionModel("_dataAccessSession", DataAccessSession.class.getCanonicalName());
        dataAccessSession.addConstructorArgument(new BeanPropertyModel("", DatabaseDialect.class.getCanonicalName(), databaseDialect));
        dataAccessSession.addConstructorArgument(new BeanPropertyModel("", StatementRegistry.class.getCanonicalName(), statementRegistry));
        dataAccessSession.addConstructorArgument(new BeanPropertyModel("", MetadataRegistry.class.getCanonicalName(), metadataContext));
        dataAccessSession.addConstructorArgument(new BeanPropertyModel(new BeanDefinitionModel("${db.dataSource}", "javax.sql.DataSource")));
        dataAccessSession.addConstructorArgument(new BeanPropertyModel("java.lang.String", "${db.username}"));
        dataAccessSession.addConstructorArgument(new BeanPropertyModel("java.lang.String", "${db.password}"));

        final BeanDefinitionModel dataAccess = new BeanDefinitionModel(SecuredDataAccess.class.getCanonicalName());
        dataAccess.addConstructorArgument(new BeanPropertyModel(dataAccessSession));
        dataAccess.addConstructorArgument(new BeanPropertyModel("", DataSecurityManager.class.getCanonicalName(), dataSecurityManager));

        model.addBean(propertyPlaceholder);
        model.addBean(metadataRegistry);
        model.addBean(databaseDialect);
        model.addBean(metadataContext);
        model.addBean(accessDeniedHandler);
        model.addBean(dataSecurityManager);
        model.addBean(statementRegistry);
        model.addBean(dataAccessSession);
        model.addBean(dataAccess);

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
