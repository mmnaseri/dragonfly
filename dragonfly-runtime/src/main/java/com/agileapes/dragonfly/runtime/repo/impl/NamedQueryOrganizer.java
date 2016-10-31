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

package com.agileapes.dragonfly.runtime.repo.impl;

import com.mmnaseri.couteau.basics.api.Filter;
import com.mmnaseri.couteau.basics.api.Processor;
import com.mmnaseri.couteau.context.error.RegistryException;
import com.mmnaseri.couteau.reflection.util.ClassUtils;
import com.mmnaseri.couteau.reflection.util.ReflectionUtils;
import com.mmnaseri.couteau.reflection.util.assets.AnnotatedElementFilter;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.error.QueryDefinitionError;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;
import com.agileapes.dragonfly.runtime.repo.EntityRepository;
import com.agileapes.dragonfly.runtime.repo.NativeQuery;
import com.agileapes.dragonfly.runtime.repo.Parameter;
import com.agileapes.dragonfly.runtime.repo.QueryAlias;
import com.agileapes.dragonfly.runtime.session.impl.SessionInitializationEventHandlerAdapter;
import com.agileapes.dragonfly.statement.StatementRegistry;
import com.agileapes.dragonfly.statement.impl.FreemarkerStatementBuilder;
import com.agileapes.dragonfly.statement.impl.StatementRegistryPreparator;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/9/1 AD, 9:32)
 */
public class NamedQueryOrganizer extends SessionInitializationEventHandlerAdapter implements BeanFactoryPostProcessor {

    private static final String TEMPLATE_NAME = "sql";
    private final Configuration configuration;
    private FreemarkerStatementBuilder statementBuilder;
    private CrudRepositoryContext crudRepositoryContext;
    private StatementRegistry statementRegistry;
    private TableMetadataRegistry tableMetadataRegistry;
    private boolean done = false;

    public NamedQueryOrganizer() {
        this.configuration = new Configuration();
    }

    @Override
    public void afterPreparingStatements(StatementRegistryPreparator preparator, TableMetadataRegistry tableMetadataRegistry, StatementRegistry statementRegistry) {
        setStatementRegistry(statementRegistry);
        setTableMetadataRegistry(tableMetadataRegistry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        setCrudRepositoryContext(beanFactory.getBean(CrudRepositoryContext.class));
        setStatementBuilder(new FreemarkerStatementBuilder(configuration, TEMPLATE_NAME, beanFactory.getBean(DatabaseDialect.class)));
    }

    private void setStatementRegistry(StatementRegistry statementRegistry) {
        this.statementRegistry = statementRegistry;
        findNamedQueries();
    }

    private void setCrudRepositoryContext(CrudRepositoryContext crudRepositoryContext) {
        this.crudRepositoryContext = crudRepositoryContext;
        findNamedQueries();
    }

    private void setStatementBuilder(FreemarkerStatementBuilder statementBuilder) {
        this.statementBuilder = statementBuilder;
        findNamedQueries();
    }

    private void setTableMetadataRegistry(TableMetadataRegistry tableMetadataRegistry) {
        this.tableMetadataRegistry = tableMetadataRegistry;
        findNamedQueries();
    }

    private boolean isReady() {
        return statementRegistry != null && crudRepositoryContext != null && statementBuilder != null && tableMetadataRegistry != null;
    }

    private boolean isDone() {
        return done;
    }

    private void findNamedQueries() {
        if (!isReady()) {
            return;
        }
        if (isDone()) {
            return;
        }
        done = true;
        final List<Class> repositories = crudRepositoryContext.getRepositories();
        for (final Class repository : repositories) {
            final Class[] typeArguments = ClassUtils.resolveTypeArguments(repository, EntityRepository.class);
            final Class entityType = typeArguments[0];
            //noinspection unchecked
            ReflectionUtils.withMethods(repository).keep(new AnnotatedElementFilter(NativeQuery.class)).each(new Processor<Method>() {
                @Override
                public void process(Method method) {
                    //noinspection unchecked
                    checkMethodParameters(method, entityType);
                    final NativeQuery query = method.getAnnotation(NativeQuery.class);
                    final String prefix = entityType.getCanonicalName() + ".";
                    final String queryName = prefix + method.getName();
                    if (query.value().isEmpty()) {
                        if (!statementRegistry.contains(queryName)) {
                            throw new QueryDefinitionError(entityType, method.getName(), "Method annotated with @NativeQuery must either declare a SQL statement or map to an existing query");
                        }
                        return;
                    }
                    final StringTemplateLoader loader = new StringTemplateLoader();
                    loader.putTemplate(TEMPLATE_NAME, query.value());
                    configuration.setTemplateLoader(loader);
                    try {
                        statementRegistry.register(queryName, statementBuilder.getStatement(tableMetadataRegistry.getTableMetadata(entityType)));
                    } catch (RegistryException e) {
                        throw new QueryDefinitionError(entityType, method.getName(), "the query defined via repository " + repository.getCanonicalName() + " could not be registered: " + e.getClass().getCanonicalName());
                    }
                }
            });
            //noinspection unchecked
            ReflectionUtils.withMethods(repository).keep(new AnnotatedElementFilter(QueryAlias.class)).each(new Processor<Method>() {
                @Override
                public void process(Method method) {
                    //noinspection unchecked
                    checkMethodParameters(method, entityType);
                    final QueryAlias queryAlias = method.getAnnotation(QueryAlias.class);
                    final String prefix = entityType.getCanonicalName() + ".";
                    final String queryName = prefix + queryAlias.value();
                    if (!statementRegistry.contains(queryName)) {
                        throw new QueryDefinitionError(entityType, method.getName(), "Method annotated with @QueryAlias must map to an existing query");
                    }
                }
            });
        }
    }

    private void checkMethodParameters(Method method, Class entityType) {
        //noinspection unchecked
        if (method.getParameterTypes().length != 1 || !entityType.isAssignableFrom(method.getParameterTypes()[0])) {
            for (Annotation[] annotations : method.getParameterAnnotations()) {
                final Annotation annotation = with(annotations).find(new Filter<Annotation>() {
                    @Override
                    public boolean accepts(Annotation item) {
                        return item.annotationType().equals(Parameter.class);
                    }
                });
                if (annotation == null) {
                    throw new QueryDefinitionError(entityType, method.getName(), "Query accessed via repository method must have @Parameter for all parameters or it must have only one parameter of the same type as the entity type");
                }
            }
        }
    }

}
