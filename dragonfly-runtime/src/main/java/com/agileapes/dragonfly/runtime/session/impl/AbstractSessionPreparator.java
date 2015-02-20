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

package com.agileapes.dragonfly.runtime.session.impl;

import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.data.DataAccessSessionAware;
import com.agileapes.dragonfly.data.DataStructureHandler;
import com.agileapes.dragonfly.data.impl.DefaultDataAccessSession;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.entity.ModifiableEntityContext;
import com.agileapes.dragonfly.entity.impl.DefaultEntityDefinitionContext;
import com.agileapes.dragonfly.entity.impl.ImmutableEntityDefinition;
import com.agileapes.dragonfly.ext.ExtensionManager;
import com.agileapes.dragonfly.ext.ExtensionMetadata;
import com.agileapes.dragonfly.ext.ExtensionMetadataResolver;
import com.agileapes.dragonfly.ext.impl.DefaultExtensionManager;
import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.metadata.impl.DefaultTableMetadataResolverContext;
import com.agileapes.dragonfly.runtime.lookup.LookupSource;
import com.agileapes.dragonfly.runtime.session.SessionInitializationEventHandler;
import com.agileapes.dragonfly.runtime.session.SessionPreparator;
import com.agileapes.dragonfly.statement.StatementRegistry;
import com.agileapes.dragonfly.statement.impl.StatementRegistryPreparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/13 AD, 18:51)
 */
public abstract class AbstractSessionPreparator implements SessionPreparator {

    private final static Log log = LogFactory.getLog(SessionPreparator.class);
    private String[] basePackages;
    private DatabaseDialect databaseDialect;
    private ModifiableEntityContext entityContext;
    private StatementRegistry statementRegistry;
    private TableMetadataRegistry tableMetadataRegistry;
    private ClassLoader parentClassLoader;
    private Set<LookupSource> entityDefinitionSources;
    private Set<LookupSource> extensionDefinitionSources;
    private final EntityDefinitionContext entityDefinitionContext;
    private final ExtensionManager extensionManager;
    private final TableMetadataResolverContext tableMetadataResolverContext;
    private boolean initializeSession;

    protected abstract List<TableMetadataResolver> getMetadataResolvers();

    protected abstract ExtensionMetadataResolver<Class<?>> getExtensionMetadataResolver();

    protected void addEntityDefinitionSource(LookupSource lookupSource) {
        entityDefinitionSources.add(lookupSource);
    }

    protected void addExtensionDefinitionSource(LookupSource lookupSource) {
        extensionDefinitionSources.add(lookupSource);
    }

    public AbstractSessionPreparator() {
        entityDefinitionSources = new CopyOnWriteArraySet<LookupSource>();
        extensionDefinitionSources = new CopyOnWriteArraySet<LookupSource>();
        entityDefinitionContext = new DefaultEntityDefinitionContext();
        extensionManager = new DefaultExtensionManager();
        tableMetadataResolverContext = new DefaultTableMetadataResolverContext(MetadataResolveStrategy.UNAMBIGUOUS, Arrays.<TableMetadataInterceptor>asList(extensionManager));
        entityDefinitionContext.addInterceptor(extensionManager);
        parentClassLoader = null;
    }

    @Override
    public void setDatabaseDialect(DatabaseDialect databaseDialect) {
        this.databaseDialect = databaseDialect;
    }

    @Override
    public void setEntityContext(ModifiableEntityContext entityContext) {
        this.entityContext = entityContext;
    }

    @Override
    public void setStatementRegistry(StatementRegistry statementRegistry) {
        this.statementRegistry = statementRegistry;
    }

    @Override
    public void setTableMetadataRegistry(TableMetadataRegistry tableMetadataRegistry) {
        this.tableMetadataRegistry = tableMetadataRegistry;
    }

    public void setParentClassLoader(ClassLoader parentClassLoader) {
        this.parentClassLoader = parentClassLoader;
    }

    public void setBasePackages(String[] basePackages) {
        this.basePackages = basePackages;
    }

    @Override
    public String[] getBasePackages() {
        return basePackages;
    }

    @Override
    public EntityDefinitionContext getEntityDefinitionContext() {
        return entityDefinitionContext;
    }

    @Override
    public TableMetadataRegistry getTableMetadataRegistry() {
        return tableMetadataRegistry;
    }

    @Override
    public ExtensionManager getExtensionManager() {
        return extensionManager;
    }

    @Override
    public DatabaseDialect getDatabaseDialect() {
        return databaseDialect;
    }

    public void setInitializeSession(boolean initializeSession) {
        this.initializeSession = initializeSession;
    }

    private void prepareResolvers(Collection<SessionInitializationEventHandler> eventHandlers) {
        final List<TableMetadataResolver> resolvers = getMetadataResolvers();
        for (SessionInitializationEventHandler eventHandler : eventHandlers) {
            eventHandler.beforePreparingResolvers(resolvers);
        }
        for (TableMetadataResolver resolver : resolvers) {
            tableMetadataResolverContext.addMetadataResolver(resolver);
        }
        for (SessionInitializationEventHandler eventHandler : eventHandlers) {
            eventHandler.afterPreparingResolvers(resolvers);
        }
    }

    private void registerEntities(Collection<SessionInitializationEventHandler> eventHandlers) {
        log.info("Finding entity classes ...");
        final List<Class> entityClasses = new ArrayList<Class>();
        for (LookupSource source : entityDefinitionSources) {
            Collections.addAll(entityClasses, source.getClasses(basePackages));
        }
        for (SessionInitializationEventHandler eventHandler : eventHandlers) {
            eventHandler.beforeRegisteringEntities(entityDefinitionContext, entityClasses);
        }
        for (Class entityClass : entityClasses) {
            log.debug("Registering entity " + entityClass.getCanonicalName());
            //noinspection unchecked
            final ImmutableEntityDefinition<Object> entityDefinition = new ImmutableEntityDefinition<Object>(entityClass, Collections.<Class<?>, Class<?>>emptyMap());
            for (SessionInitializationEventHandler eventHandler : eventHandlers) {
                eventHandler.beforeRegisteringEntity(entityDefinitionContext, entityDefinition);
            }
            entityDefinitionContext.addDefinition(entityDefinition);
            for (SessionInitializationEventHandler eventHandler : eventHandlers) {
                eventHandler.afterRegisteringEntity(entityDefinitionContext, entityDefinition);
            }
        }
        for (SessionInitializationEventHandler eventHandler : eventHandlers) {
            eventHandler.afterRegisteringEntities(entityDefinitionContext, entityClasses);
        }
    }

    private void registerExtensions(Collection<SessionInitializationEventHandler> eventHandlers) {
        log.info("Finding extensions to the data access ...");
        final ExtensionMetadataResolver<Class<?>> metadataResolver = getExtensionMetadataResolver();
        final List<Class> extensionClasses = new ArrayList<Class>();
        for (LookupSource source : extensionDefinitionSources) {
            Collections.addAll(extensionClasses, source.getClasses(basePackages));
        }
        for (SessionInitializationEventHandler eventHandler : eventHandlers) {
            eventHandler.beforeRegisteringExtensions(extensionManager, extensionClasses);
        }
        for (Class extensionDefinitionClass : extensionClasses) {
            log.debug("Registering extension " + extensionDefinitionClass.getCanonicalName());
            final ExtensionMetadata extensionMetadata = metadataResolver.resolve(extensionDefinitionClass);
            for (SessionInitializationEventHandler eventHandler : eventHandlers) {
                eventHandler.beforeRegisteringExtension(extensionManager, extensionMetadata);
            }
            extensionManager.addExtension(extensionMetadata);
            for (SessionInitializationEventHandler eventHandler : eventHandlers) {
                eventHandler.afterRegisteringExtension(extensionManager, extensionMetadata);
            }
        }
        for (SessionInitializationEventHandler eventHandler : eventHandlers) {
            eventHandler.beforeRegisteringExtensions(extensionManager, extensionClasses);
        }
    }

    private void prepareStatements(Collection<SessionInitializationEventHandler> eventHandlers) {
        log.info("Preparing entity statements for later use");
        final StatementRegistryPreparator preparator = new StatementRegistryPreparator(databaseDialect, tableMetadataResolverContext, tableMetadataRegistry);
        for (Class<?> entityDefinitionClass : entityDefinitionContext.getEntities()) {
            preparator.addEntity(entityDefinitionClass);
        }
        for (SessionInitializationEventHandler eventHandler : eventHandlers) {
            eventHandler.beforePreparingStatements(preparator, tableMetadataRegistry, statementRegistry);
        }
        preparator.prepare(statementRegistry);
        for (SessionInitializationEventHandler eventHandler : eventHandlers) {
            eventHandler.afterPreparingStatements(preparator, tableMetadataRegistry, statementRegistry);
        }
    }

    private void registerInterfaces(final Collection<SessionInitializationEventHandler> eventHandlers) {
        log.info("Registering interfaces with the context");
        entityContext.setInterfaces(with(entityDefinitionContext.getEntities()).map(new Transformer<Class<?>, Map<Class<?>, Class<?>>>() {
            @Override
            public Map<Class<?>, Class<?>> map(Class<?> input) {
                //noinspection unchecked
                final EntityDefinition<Object> definition = extensionManager.intercept(new ImmutableEntityDefinition<Object>((Class<Object>) input, Collections.<Class<?>, Class<?>>emptyMap()));
                final Map<Class<?>, Class<?>> interfaces = definition.getInterfaces();
                for (SessionInitializationEventHandler eventHandler : eventHandlers) {
                    eventHandler.beforeDeterminingInterfaces(input, interfaces);
                }
                return interfaces;
            }
        }));
    }

    private void initializeSession(DataAccessSession session, ConfigurableListableBeanFactory beanFactory, Collection<SessionInitializationEventHandler> eventHandlers) {
        if (initializeSession) {
            final DataStructureHandler dataStructureHandler;
            if (session instanceof DefaultDataAccessSession) {
                dataStructureHandler = ((DefaultDataAccessSession) session).getDataStructureHandler();
            } else {
                dataStructureHandler = null;
            }
            for (SessionInitializationEventHandler eventHandler : eventHandlers) {
                eventHandler.beforeInitialization(beanFactory, session, dataStructureHandler);
            }
            session.initialize();
            for (SessionInitializationEventHandler eventHandler : eventHandlers) {
                eventHandler.beforeSignalingInitialization(beanFactory, session, dataStructureHandler);
            }
            session.markInitialized();
            for (SessionInitializationEventHandler eventHandler : eventHandlers) {
                eventHandler.afterInitialization(beanFactory, session, dataStructureHandler);
            }
        }
    }

    private void handleClassLoader(ConfigurableListableBeanFactory beanFactory) {
        log.info("Setting the class loader for the entity context");
        entityContext.setDefaultClassLoader(beanFactory.getBeanClassLoader());
    }

    @Override
    public void postProcessSession(ConfigurableListableBeanFactory beanFactory) {
        log.warn("Preparing the session at runtime can be harmful to your performance. You should consider switching to " +
                "the Maven plugin.");
        log.debug("Looking up the necessary components ...");
        if (parentClassLoader == null) {
            log.debug("Falling back to the application context class loader");
            parentClassLoader = beanFactory.getBeanClassLoader();
        }
        final DataAccessSession session = beanFactory.getBean(DataAccessSession.class);
        final Collection<SessionInitializationEventHandler> eventHandlers = beanFactory.getBeansOfType(SessionInitializationEventHandler.class, false, true).values();
        for (SessionInitializationEventHandler eventHandler : eventHandlers) {
            if (eventHandler instanceof DataAccessSessionAware) {
                DataAccessSessionAware aware = (DataAccessSessionAware) eventHandler;
                aware.setDataAccessSession(session);
            }
        }
        prepareResolvers(eventHandlers);
        registerEntities(eventHandlers);
        registerExtensions(eventHandlers);
        prepareStatements(eventHandlers);
        registerInterfaces(eventHandlers);
        initializeSession(session, beanFactory, eventHandlers);
        handleClassLoader(beanFactory);
    }

}
