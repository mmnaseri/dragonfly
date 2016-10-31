/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.agileapes.dragonfly.runtime.session;

import com.mmnaseri.couteau.basics.api.Transformer;
import com.mmnaseri.couteau.basics.api.impl.CastingTransformer;
import com.mmnaseri.couteau.reflection.util.ClassUtils;
import com.agileapes.dragonfly.annotations.Extension;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.entity.ModifiableEntityContext;
import com.agileapes.dragonfly.entity.impl.DefaultEntityDefinitionContext;
import com.agileapes.dragonfly.entity.impl.ImmutableEntityDefinition;
import com.agileapes.dragonfly.ext.ExtensionManager;
import com.agileapes.dragonfly.ext.impl.AnnotationExtensionMetadataResolver;
import com.agileapes.dragonfly.ext.impl.DefaultExtensionManager;
import com.agileapes.dragonfly.metadata.MetadataResolveStrategy;
import com.agileapes.dragonfly.metadata.TableMetadataInterceptor;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;
import com.agileapes.dragonfly.metadata.TableMetadataResolverContext;
import com.agileapes.dragonfly.metadata.impl.AnnotationTableMetadataResolver;
import com.agileapes.dragonfly.metadata.impl.DefaultTableMetadataResolverContext;
import com.agileapes.dragonfly.statement.StatementRegistry;
import com.agileapes.dragonfly.statement.impl.StatementRegistryPreparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.persistence.Entity;
import java.util.*;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/18, 17:56)
 */
@Deprecated
public class ApplicationSessionPreparator implements BeanFactoryPostProcessor {

    private final static Log log = LogFactory.getLog(ApplicationSessionPreparator.class);

    private final ExtensionManager extensionManager;
    private final TableMetadataResolverContext resolverContext;
    private final EntityDefinitionContext definitionContext;
    private final String[] basePackages;
    private final DatabaseDialect databaseDialect;
    private TableMetadataRegistry tableMetadataRegistry;
    private StatementRegistry statementRegistry;
    private ClassLoader parentClassLoader;
    private AnnotationTableMetadataResolver metadataResolver;
    private final boolean initializeSession;

    public ApplicationSessionPreparator(String basePackages, DatabaseDialect databaseDialect, boolean initializeSession) {
        this(basePackages.split(","), databaseDialect, initializeSession);
    }

    public ApplicationSessionPreparator(Properties basePackages, DatabaseDialect databaseDialect, boolean initializeSession) {
        this(with(basePackages.keySet()).transform(new CastingTransformer<Object, String>(String.class)).list(), databaseDialect, initializeSession);
    }

    public ApplicationSessionPreparator(Collection<String> basePackages, DatabaseDialect databaseDialect, boolean initializeSession) {
        this(basePackages.toArray(new String[basePackages.size()]), databaseDialect, initializeSession);
    }

    public ApplicationSessionPreparator(String[] basePackages, DatabaseDialect databaseDialect, boolean initializeSession) {
        this.basePackages = basePackages;
        this.databaseDialect = databaseDialect;
        this.initializeSession = initializeSession;
        extensionManager = new DefaultExtensionManager();
        resolverContext = new DefaultTableMetadataResolverContext(MetadataResolveStrategy.UNAMBIGUOUS, Arrays.<TableMetadataInterceptor>asList(extensionManager));
        definitionContext = new DefaultEntityDefinitionContext();
        definitionContext.addInterceptor(extensionManager);
        metadataResolver = new AnnotationTableMetadataResolver(databaseDialect);
        resolverContext.addMetadataResolver(metadataResolver);
        parentClassLoader = null;
    }

    public void setParentClassLoader(ClassLoader parentClassLoader) {
        this.parentClassLoader = parentClassLoader;
    }

    public ExtensionManager getExtensionManager() {
        return extensionManager;
    }

    public TableMetadataResolverContext getResolverContext() {
        return resolverContext;
    }

    public EntityDefinitionContext getDefinitionContext() {
        return definitionContext;
    }

    public String[] getBasePackages() {
        return basePackages;
    }

    public TableMetadataRegistry getTableMetadataRegistry() {
        return tableMetadataRegistry;
    }

    public StatementRegistry getStatementRegistry() {
        return statementRegistry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory context) throws BeansException {
        showDeprecationWarning();
        long time = System.nanoTime();
        log.warn("Preparing the session at runtime can be harmful to your performance. You should consider switching to " +
                "the Maven plugin.");
        log.debug("Looking up the necessary components ...");
        tableMetadataRegistry = context.getBean(TableMetadataRegistry.class);
        statementRegistry = context.getBean(StatementRegistry.class);
        final ModifiableEntityContext entityContext = context.getBean(ModifiableEntityContext.class);
        final ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(false);
        log.info("Finding entity classes ...");
        log.debug("Looking for classes with @Entity");
        componentProvider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        if (parentClassLoader == null) {
            log.debug("Falling back to the application context class loader");
            parentClassLoader = context.getBeanClassLoader();
        }
        for (String basePackage : basePackages) {
            final Set<BeanDefinition> beanDefinitions = componentProvider.findCandidateComponents(basePackage);
            for (BeanDefinition beanDefinition : beanDefinitions) {
                try {
                    log.debug("Registering entity " + beanDefinition.getBeanClassName());
                    //noinspection unchecked
                    definitionContext.addDefinition(new ImmutableEntityDefinition<Object>(ClassUtils.forName(beanDefinition.getBeanClassName(), parentClassLoader), Collections.<Class<?>, Class<?>>emptyMap()));
                } catch (ClassNotFoundException e) {
                    throw new FatalBeanException("Failed to retrieve class: " + beanDefinition.getBeanClassName(), e);
                }
            }
        }
        componentProvider.resetFilters(false);
        log.info("Finding extensions to the data access ...");
        log.debug("Looking for classes with @Extension");
        componentProvider.addIncludeFilter(new AnnotationTypeFilter(Extension.class));
        final AnnotationExtensionMetadataResolver extensionMetadataResolver = new AnnotationExtensionMetadataResolver(metadataResolver);
        for (String basePackage : basePackages) {
            final Set<BeanDefinition> beanDefinitions = componentProvider.findCandidateComponents(basePackage);
            for (BeanDefinition beanDefinition : beanDefinitions) {
                try {
                    log.debug("Registering extension " + beanDefinition.getBeanClassName());
                    extensionManager.addExtension(extensionMetadataResolver.resolve(ClassUtils.forName(beanDefinition.getBeanClassName(), parentClassLoader)));
                } catch (ClassNotFoundException e) {
                    throw new FatalBeanException("Failed to retrieve class: " + beanDefinition.getBeanClassName(), e);
                }
            }
        }
        log.info("Preparing entity statements for later use");
        final StatementRegistryPreparator preparator = new StatementRegistryPreparator(databaseDialect, resolverContext, tableMetadataRegistry);
        for (Class<?> entity : definitionContext.getEntities()) {
            preparator.addEntity(entity);
        }
        preparator.prepare(statementRegistry);
        log.info("Registering interfaces with the context");
        entityContext.setInterfaces(with(definitionContext.getEntities()).map(new Transformer<Class<?>, Map<Class<?>, Class<?>>>() {
            @Override
            public Map<Class<?>, Class<?>> map(Class<?> input) {
                //noinspection unchecked
                final EntityDefinition<Object> definition = extensionManager.intercept(new ImmutableEntityDefinition<Object>((Class<Object>) input, Collections.<Class<?>, Class<?>>emptyMap()));
                return definition.getInterfaces();
            }
        }));
        if (initializeSession) {
            final DataAccessSession session = context.getBean(DataAccessSession.class);
            session.initialize();
            session.markInitialized();
        }
        log.info("Setting the class loader for the entity context");
        entityContext.setDefaultClassLoader(context.getBeanClassLoader());
        time = System.nanoTime() - time;
        log.info("Session preparation took " + Math.round((double) time / 1000000d) / 1000d + " second(s)");
    }

    private void showDeprecationWarning() {
        System.out.flush();
        System.err.println("\n\n\n\n");
        System.err.println("DEPRECATED METHOD IN USE");
        System.err.println("------------------------");
        System.err.println("Please note that you are initializing the persistence unit via a deprecated method and\n" +
                "as such your setup is not guaranteed to work as you expect. In this mode, you should also deactivate\n" +
                "scanning for @Configuration classes via the XML based configuration for Spring as it might lead to multiple\n" +
                "instances of session-related metadata collectors to be created.");
        System.err.println("");
        for (int i = 5; i >= 0; i --) {
            try {
                System.err.print("This notice will go away in " + i + " more second(s) ...\r");
                System.err.flush();
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }
        System.err.print("\t\t\t\t\t\t\t\t\t\t\t\r");
        System.err.println("");
    }

}
