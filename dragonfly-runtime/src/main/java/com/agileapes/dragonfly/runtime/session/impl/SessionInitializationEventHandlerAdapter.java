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

import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.data.DataStructureHandler;
import com.agileapes.dragonfly.entity.EntityDefinition;
import com.agileapes.dragonfly.entity.EntityDefinitionContext;
import com.agileapes.dragonfly.ext.ExtensionManager;
import com.agileapes.dragonfly.ext.ExtensionMetadata;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;
import com.agileapes.dragonfly.metadata.TableMetadataResolver;
import com.agileapes.dragonfly.runtime.session.SessionInitializationEventHandler;
import com.agileapes.dragonfly.statement.StatementRegistry;
import com.agileapes.dragonfly.statement.impl.StatementRegistryPreparator;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Collection;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/28 AD, 14:47)
 */
public abstract class SessionInitializationEventHandlerAdapter implements SessionInitializationEventHandler {

    @Override
    public void beforeInitialization(ConfigurableListableBeanFactory beanFactory, DataAccessSession session, DataStructureHandler dataStructureHandler) {

    }

    @Override
    public void afterInitialization(ConfigurableListableBeanFactory beanFactory, DataAccessSession session, DataStructureHandler dataStructureHandler) {

    }

    @Override
    public void beforePreparingResolvers(Collection<TableMetadataResolver> resolvers) {

    }

    @Override
    public void afterPreparingResolvers(Collection<TableMetadataResolver> resolvers) {

    }

    @Override
    public void beforeRegisteringEntities(EntityDefinitionContext entityDefinitionContext, Collection<Class> entityClasses) {

    }

    @Override
    public void afterRegisteringEntities(EntityDefinitionContext entityDefinitionContext, Collection<Class> entityClasses) {

    }

    @Override
    public void beforeRegisteringEntity(EntityDefinitionContext entityDefinitionContext, EntityDefinition<?> entityDefinition) {

    }

    @Override
    public void afterRegisteringEntity(EntityDefinitionContext entityDefinitionContext, EntityDefinition<?> entityDefinition) {

    }

    @Override
    public void beforeRegisteringExtensions(ExtensionManager extensionManager, Collection<Class> extensionClasses) {

    }

    @Override
    public void beforeRegisteringExtension(ExtensionManager extensionManager, ExtensionMetadata extensionMetadata) {

    }

    @Override
    public void afterRegisteringExtension(ExtensionManager extensionManager, ExtensionMetadata extensionMetadata) {

    }

    @Override
    public void beforePreparingStatements(StatementRegistryPreparator preparator, TableMetadataRegistry tableMetadataRegistry, StatementRegistry statementRegistry) {

    }

    @Override
    public void afterPreparingStatements(StatementRegistryPreparator preparator, TableMetadataRegistry tableMetadataRegistry, StatementRegistry statementRegistry) {

    }

    @Override
    public void beforeDeterminingInterfaces(Class<?> entityType, Map<Class<?>, Class<?>> interfaces) {

    }

    @Override
    public void beforeSignalingInitialization(ConfigurableListableBeanFactory beanFactory, DataAccessSession session, DataStructureHandler dataStructureHandler) {

    }

}
