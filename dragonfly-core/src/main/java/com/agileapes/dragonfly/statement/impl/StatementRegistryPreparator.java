package com.agileapes.dragonfly.statement.impl;

import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.error.MetadataCollectionError;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.metadata.MetadataResolver;
import com.agileapes.dragonfly.metadata.NamedQueryMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
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
    private final StringTemplateLoader loader;

    public StatementRegistryPreparator(DatabaseDialect dialect, MetadataResolver resolver, MetadataRegistry registry) {
        this.dialect = dialect;
        this.resolver = resolver;
        this.registry = registry;
        this.entities = new CopyOnWriteArraySet<Class<?>>();
        this.loader = new StringTemplateLoader();
        final Configuration configuration = new Configuration();
        configuration.setTemplateLoader(loader);
        this.statementBuilder = new FreemarkerStatementBuilder(configuration, "sql", this.dialect);
    }

    public void addEntity(Class<?> entity) {
        entities.add(entity);
    }

    public void prepare(StatementRegistry statementRegistry) {
        for (Class<?> entity : entities) {
            registry.addTableMetadata(resolver.resolve(entity));
        }
        for (Class<?> entity : entities) {
            final TableMetadata<?> tableMetadata = registry.getTableMetadata(entity);
            try {
                statementRegistry.register(entity.getCanonicalName() + ".deleteAll", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.DELETE_ALL).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".deleteByKey", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.DELETE_ONE).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".deleteLike", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.DELETE_LIKE).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".findAll", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.FIND_ALL).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".findByKey", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.FIND_ONE).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".findLike", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.FIND_LIKE).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".insert", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.INSERT).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".updateBySample", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.UPDATE).getStatement(tableMetadata));
                statementRegistry.register(entity.getCanonicalName() + ".truncate", dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.TRUNCATE).getStatement(tableMetadata));
                for (NamedQueryMetadata namedQueryMetadata : tableMetadata.getNamedQueries()) {
                    loader.putTemplate("sql", namedQueryMetadata.getQuery());
                    statementRegistry.register(entity.getCanonicalName() + "." + namedQueryMetadata.getName(), statementBuilder.getStatement(tableMetadata));
                }

            } catch (RegistryException e) {
                throw new MetadataCollectionError("Failed to prepare statements for entity " + entity.getCanonicalName(), e);
            }
        }
    }

}
