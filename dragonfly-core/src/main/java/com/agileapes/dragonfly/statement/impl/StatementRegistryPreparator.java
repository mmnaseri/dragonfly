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

package com.agileapes.dragonfly.statement.impl;

import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.error.MetadataCollectionError;
import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.statement.StatementBuilder;
import com.agileapes.dragonfly.statement.Statements;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 19:58)
 */
public class StatementRegistryPreparator {

    private final DatabaseDialect dialect;
    private final MetadataResolver resolver;
    private final MetadataRegistry registry;
    private final Set<Class<?>> entities;
    private final StatementBuilder statementBuilder;
    private final Configuration configuration;

    public StatementRegistryPreparator(DatabaseDialect dialect, MetadataResolver resolver, MetadataRegistry registry) {
        this.dialect = dialect;
        this.resolver = resolver;
        this.registry = registry;
        this.entities = new CopyOnWriteArraySet<Class<?>>();
        this.configuration = new Configuration();
        this.statementBuilder = new FreemarkerStatementBuilder(configuration, "sql", this.dialect);
    }

    public void addEntity(Class<?> entity) {
        entities.add(entity);
    }

    public void prepare(StatementRegistry statementRegistry) {
        for (Class<?> entity : entities) {
            if (!registry.contains(entity)) {
                registry.addTableMetadata(resolver.resolve(entity));
            }
        }
        entities.clear();
        entities.addAll(registry.getEntityTypes());
        for (Class<?> entity : entities) {
            final TableMetadata<?> tableMetadata = registry.getTableMetadata(entity);
            try {
                statementRegistry.register(entity.getCanonicalName() + ".deleteAll", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.DELETE_ALL).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".deleteByKey", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.DELETE_ONE).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".deleteLike", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.DELETE_LIKE).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".findAll", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.FIND_ALL).getStatement(tableMetadata, (Metadata) tableMetadata.getOrdering()));
                statementRegistry.register(entity.getCanonicalName() + ".findByKey", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.FIND_ONE).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".findLike", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.FIND_LIKE).getStatement(tableMetadata, (Metadata) tableMetadata.getOrdering()));
                statementRegistry.register(entity.getCanonicalName() + ".countAll", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.COUNT_ALL).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".countByKey", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.COUNT_ONE).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".countLike", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.COUNT_LIKE).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".insert", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.INSERT).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".updateBySample", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.UPDATE).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".truncate", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.TRUNCATE).getStatement(tableMetadata));
                for (NamedQueryMetadata namedQueryMetadata : tableMetadata.getNamedQueries()) {
                    final StringTemplateLoader loader = new StringTemplateLoader();
                    loader.putTemplate("sql", namedQueryMetadata.getQuery());
                    configuration.setTemplateLoader(loader);
                    statementRegistry.register(entity.getCanonicalName() + "." + namedQueryMetadata.getName(), statementBuilder.getStatement(tableMetadata));
                }
                for (StoredProcedureMetadata procedure : tableMetadata.getProcedures()) {
                    statementRegistry.register(entity.getCanonicalName() + ".call." + procedure.getName(), new ProcedureCallStatement(dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.CALL).getStatement(tableMetadata, procedure), dialect));
                }
            } catch (RegistryException e) {
                throw new MetadataCollectionError("Failed to prepare statements for entity " + entity.getCanonicalName(), e);
            }
        }
    }

}
