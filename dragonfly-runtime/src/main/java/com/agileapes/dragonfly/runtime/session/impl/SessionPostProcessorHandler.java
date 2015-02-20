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

import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.entity.ModifiableEntityContext;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;
import com.agileapes.dragonfly.runtime.session.*;
import com.agileapes.dragonfly.statement.StatementRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/13 AD, 18:45)
 */
public class SessionPostProcessorHandler implements BeanFactoryPostProcessor, DatabaseDialectAware, EntityContextAware, StatementRegistryAware, TableMetadataRegistryAware {

    private final static Log log = LogFactory.getLog(SessionPostProcessorHandler.class);

    private DatabaseDialect databaseDialect = null;
    private StatementRegistry statementRegistry = null;
    private TableMetadataRegistry tableMetadataRegistry = null;
    private ModifiableEntityContext entityContext = null;

    @Override
    public void setDatabaseDialect(DatabaseDialect databaseDialect) {
        this.databaseDialect = databaseDialect;
    }

    @Override
    public void setStatementRegistry(StatementRegistry statementRegistry) {
        this.statementRegistry = statementRegistry;
    }

    @Override
    public void setTableMetadataRegistry(TableMetadataRegistry tableMetadataRegistry) {
        this.tableMetadataRegistry = tableMetadataRegistry;
    }

    @Override
    public void setEntityContext(ModifiableEntityContext entityContext) {
        this.entityContext = entityContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        final ArrayList<SessionPostProcessor> postProcessors = new ArrayList<SessionPostProcessor>(beanFactory.getBeansOfType(SessionPostProcessor.class, false, true).values());
        Collections.sort(postProcessors, new Comparator<SessionPostProcessor>() {
            @Override
            public int compare(SessionPostProcessor first, SessionPostProcessor second) {
                Integer firstOrder = first instanceof Ordered ? ((Ordered) first).getOrder() : 0;
                Integer secondOrder = second instanceof Ordered ? ((Ordered) second).getOrder() : 0;
                return firstOrder.compareTo(secondOrder);
            }
        });
        long time = System.nanoTime();
        for (SessionPostProcessor postProcessor : postProcessors) {
            if (postProcessor instanceof EntityContextAware) {
                ((EntityContextAware) postProcessor).setEntityContext(getEntityContext(beanFactory));
            }
            if (postProcessor instanceof TableMetadataRegistryAware) {
                ((TableMetadataRegistryAware) postProcessor).setTableMetadataRegistry(getTableMetadataRegistry(beanFactory));
            }
            if (postProcessor instanceof StatementRegistryAware) {
                ((StatementRegistryAware) postProcessor).setStatementRegistry(getStatementRegistry(beanFactory));
            }
            if (postProcessor instanceof DatabaseDialectAware) {
                ((DatabaseDialectAware) postProcessor).setDatabaseDialect(getDatabaseDialect(beanFactory));
            }
            postProcessor.postProcessSession(beanFactory);
        }
        time = System.nanoTime() - time;
        log.info("Session preparation took " + Math.round((double) time / 1000000d) / 1000d + " second(s)");
    }

    private ModifiableEntityContext getEntityContext(ConfigurableListableBeanFactory beanFactory) {
        return entityContext = entityContext != null ? entityContext : beanFactory.getBean(ModifiableEntityContext.class);
    }

    private TableMetadataRegistry getTableMetadataRegistry(ConfigurableListableBeanFactory beanFactory) {
        return tableMetadataRegistry = tableMetadataRegistry != null ? tableMetadataRegistry : beanFactory.getBean(TableMetadataRegistry.class);
    }

    private StatementRegistry getStatementRegistry(ConfigurableListableBeanFactory beanFactory) {
        return statementRegistry = statementRegistry != null ? statementRegistry : beanFactory.getBean(StatementRegistry.class);
    }

    private DatabaseDialect getDatabaseDialect(ConfigurableListableBeanFactory beanFactory) {
        return databaseDialect = databaseDialect != null ? databaseDialect : beanFactory.getBean(DatabaseDialect.class);
    }
}
