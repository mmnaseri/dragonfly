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
import com.agileapes.couteau.freemarker.utils.FreemarkerUtils;
import com.agileapes.couteau.maven.resource.ProjectClassResourceTransformer;
import com.agileapes.couteau.maven.resource.ProjectResourceType;
import com.agileapes.couteau.maven.resource.ProjectResourceTypeFilter;
import com.agileapes.couteau.maven.task.PluginTask;
import com.agileapes.couteau.reflection.util.assets.AssignableTypeFilter;
import com.agileapes.dragonfly.data.DataAccessPostProcessor;
import com.agileapes.dragonfly.data.impl.DefaultDataAccessSession;
import com.agileapes.dragonfly.data.impl.SecuredDataAccess;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.entity.EntityHandlerContext;
import com.agileapes.dragonfly.entity.impl.DefaultEntityContext;
import com.agileapes.dragonfly.entity.impl.DefaultEntityHandlerContext;
import com.agileapes.dragonfly.io.OutputManager;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;
import com.agileapes.dragonfly.metadata.impl.DefaultTableMetadataContext;
import com.agileapes.dragonfly.model.ApplicationContextModel;
import com.agileapes.dragonfly.model.BeanDefinitionModel;
import com.agileapes.dragonfly.model.BeanPropertyModel;
import com.agileapes.dragonfly.mojo.PluginExecutor;
import com.agileapes.dragonfly.security.AccessDeniedHandler;
import com.agileapes.dragonfly.security.DataSecurityManager;
import com.agileapes.dragonfly.security.impl.DefaultDataSecurityManager;
import com.agileapes.dragonfly.security.impl.FailFirstAccessDeniedHandler;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;
import freemarker.template.Template;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 1:13)
 */
@Component
public class GenerateSetupContextTask extends PluginTask<PluginExecutor> {

    @Autowired
    private OutputManager outputManager;

    @Autowired
    private EntityDefinitionContext definitionContext;

    @Override
    protected String getIntro() {
        return "Generating setup application context";
    }

    @Value("#{defineEntities}")
    @Override
    public void setDependencies(Collection<PluginTask<PluginExecutor>> dependencies) {
        super.setDependencies(dependencies);
    }

    @Override
    public void execute(PluginExecutor executor) throws MojoFailureException {
        final ApplicationContextModel model = new ApplicationContextModel();

        final BeanDefinitionModel propertyPlaceholder = new BeanDefinitionModel(PropertyPlaceholderConfigurer.class.getCanonicalName());
        propertyPlaceholder.setProperty(new BeanPropertyModel("locations", "classpath:db.properties"));

        final BeanDefinitionModel metadataRegistry = new BeanDefinitionModel("_generatedMetadataRegistry", GenerateMetadataRegistryTask.CLASS_NAME);

        final BeanDefinitionModel databaseDialect = new BeanDefinitionModel("_databaseDialect", executor.getDialect().getClass().getCanonicalName());

        final BeanDefinitionModel metadataContext = new BeanDefinitionModel("metadataContext", DefaultTableMetadataContext.class.getCanonicalName());
        metadataContext.setProperty(new BeanPropertyModel("registries", metadataRegistry));

        final BeanDefinitionModel accessDeniedHandler = new BeanDefinitionModel("_accessDeniedHandler", FailFirstAccessDeniedHandler.class.getCanonicalName());

        final BeanDefinitionModel dataSecurityManager = new BeanDefinitionModel("_dataSecurityManager", DefaultDataSecurityManager.class.getCanonicalName());
        dataSecurityManager.addConstructorArgument(new BeanPropertyModel("", AccessDeniedHandler.class.getCanonicalName(), accessDeniedHandler));

        final BeanDefinitionModel statementRegistry = new BeanDefinitionModel("_generatedStatementRegistry", "com.agileapes.dragonfly.statement.GeneratedStatementRegistry");
        statementRegistry.addConstructorArgument(new BeanPropertyModel("", DatabaseDialect.class.getCanonicalName(), databaseDialect));
        statementRegistry.addConstructorArgument(new BeanPropertyModel("", TableMetadataRegistry.class.getCanonicalName(), metadataContext));

        final BeanDefinitionModel dataAccessSession = new BeanDefinitionModel("_dataAccessSession", DefaultDataAccessSession.class.getCanonicalName());
        dataAccessSession.addConstructorArgument(new BeanPropertyModel("", DatabaseDialect.class.getCanonicalName(), databaseDialect));
        dataAccessSession.addConstructorArgument(new BeanPropertyModel("", StatementRegistry.class.getCanonicalName(), statementRegistry));
        dataAccessSession.addConstructorArgument(new BeanPropertyModel("", TableMetadataRegistry.class.getCanonicalName(), metadataContext));
        dataAccessSession.addConstructorArgument(new BeanPropertyModel(new BeanDefinitionModel("${db.dataSource}", "javax.sql.DataSource")));
        dataAccessSession.addConstructorArgument(new BeanPropertyModel("java.lang.String", "${db.username}"));
        dataAccessSession.addConstructorArgument(new BeanPropertyModel("java.lang.String", "${db.password}"));

        final HashMap<String, BeanPropertyModel> interfaces = new HashMap<String, BeanPropertyModel>();
        final HashMap<String, BeanDefinitionModel> entityFactories = new HashMap<String, BeanDefinitionModel>();
        final BeanDefinitionModel entityContext = new BeanDefinitionModel(DefaultEntityContext.class.getCanonicalName());
        entityContext.addConstructorArgument(new BeanPropertyModel("", DataSecurityManager.class.getCanonicalName(), dataSecurityManager));
        entityContext.addConstructorArgument(new BeanPropertyModel("", TableMetadataRegistry.class.getCanonicalName(), metadataContext));
        entityContext.setProperty(new BeanPropertyModel("interfaces", interfaces));
        entityContext.setProperty(new BeanPropertyModel("entityFactories", entityFactories));
        for (EntityDefinition<?> definition : definitionContext.getDefinitions()) {
            entityFactories.put(definition.getEntityType().getCanonicalName(), new BeanDefinitionModel(definition.getEntityType().getCanonicalName().concat(GenerateEntityFactoryTask.ENTITY_FACTORY_SUFFIX)));
            final HashMap<String, String> value = new HashMap<String, String>();
            interfaces.put(definition.getEntityType().getCanonicalName(), new BeanPropertyModel(value));
            final Map<Class<?>,Class<?>> map = definition.getInterfaces();
            for (Map.Entry<Class<?>, Class<?>> entry : map.entrySet()) {
                value.put(entry.getKey().getCanonicalName(), entry.getValue().getCanonicalName());
            }
        }

        final BeanDefinitionModel entityHandlerContext = new BeanDefinitionModel(DefaultEntityHandlerContext.class.getCanonicalName());
        entityHandlerContext.addConstructorArgument(new BeanPropertyModel("", EntityContext.class.getCanonicalName(), entityContext));
        entityHandlerContext.addConstructorArgument(new BeanPropertyModel("", TableMetadataRegistry.class.getCanonicalName(), metadataContext));

        final BeanDefinitionModel dataAccess = new BeanDefinitionModel(SecuredDataAccess.class.getCanonicalName());
        dataAccess.addConstructorArgument(new BeanPropertyModel(dataAccessSession));
        dataAccess.addConstructorArgument(new BeanPropertyModel("", DataSecurityManager.class.getCanonicalName(), dataSecurityManager));
        dataAccess.addConstructorArgument(new BeanPropertyModel("", EntityContext.class.getCanonicalName(), entityContext));
        dataAccess.addConstructorArgument(new BeanPropertyModel("", EntityHandlerContext.class.getCanonicalName(), entityHandlerContext));

        model.addBean(propertyPlaceholder);
        model.addBean(metadataRegistry);
        model.addBean(databaseDialect);
        model.addBean(metadataContext);
        model.addBean(accessDeniedHandler);
        model.addBean(dataSecurityManager);
        model.addBean(statementRegistry);
        model.addBean(dataAccessSession);
        model.addBean(dataAccess);
        model.addBean(entityContext);
        model.addBean(entityHandlerContext);

        //noinspection unchecked
        with(executor.getProjectResources())
                .keep(new ProjectResourceTypeFilter(ProjectResourceType.CLASS))
                .transform(new ProjectClassResourceTransformer())
                .keep(new AssignableTypeFilter(DataAccessPostProcessor.class))
                .each(new Processor<Class<?>>() {
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
            e.printStackTrace();
            throw new MojoFailureException("Failed to produce application context XML", e);
        }
    }

}
