/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
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

package com.mmnaseri.dragonfly.runtime.ext.monitoring.impl;

import com.mmnaseri.couteau.context.contract.Registry;
import com.mmnaseri.couteau.context.error.RegistryException;
import com.mmnaseri.couteau.context.impl.ConcurrentRegistry;
import com.mmnaseri.couteau.freemarker.utils.FreemarkerUtils;
import com.mmnaseri.dragonfly.data.DataAccess;
import com.mmnaseri.dragonfly.data.DataAccessSession;
import com.mmnaseri.dragonfly.data.DataStructureHandler;
import com.mmnaseri.dragonfly.data.OperationType;
import com.mmnaseri.dragonfly.dialect.DatabaseDialect;
import com.mmnaseri.dragonfly.entity.*;
import com.mmnaseri.dragonfly.entity.impl.DefaultEntityMapCreator;
import com.mmnaseri.dragonfly.entity.impl.DefaultRowHandler;
import com.mmnaseri.dragonfly.error.UnsuccessfulOperationError;
import com.mmnaseri.dragonfly.error.UnsupportedStatementTypeError;
import com.mmnaseri.dragonfly.error.VersionColumnDefinitionError;
import com.mmnaseri.dragonfly.fluent.tools.QueryBuilderTools;
import com.mmnaseri.dragonfly.metadata.impl.DefaultTableMetadataRegistry;
import com.mmnaseri.dragonfly.metadata.impl.ResolvedColumnMetadata;
import com.mmnaseri.dragonfly.metadata.impl.ResolvedTableMetadata;
import com.mmnaseri.dragonfly.metadata.impl.UnresolvedTableMetadata;
import com.mmnaseri.dragonfly.runtime.ext.monitoring.*;
import com.mmnaseri.dragonfly.runtime.session.impl.SessionInitializationEventHandlerAdapter;
import com.mmnaseri.dragonfly.statement.StatementBuilder;
import com.mmnaseri.dragonfly.statement.StatementRegistry;
import com.mmnaseri.dragonfly.statement.Statements;
import com.mmnaseri.dragonfly.statement.impl.DefaultStatementRegistry;
import com.mmnaseri.dragonfly.statement.impl.FreemarkerStatementBuilder;
import com.mmnaseri.dragonfly.statement.impl.LocalStatementRegistry;
import com.mmnaseri.dragonfly.statement.impl.StatementRegistryPreparator;
import com.mmnaseri.dragonfly.tools.ColumnNameFilter;
import com.mmnaseri.dragonfly.tools.DatabaseUtils;
import com.mmnaseri.dragonfly.tools.MapTools;
import com.mmnaseri.dragonfly.metadata.*;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.io.Serializable;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/28 AD, 14:48)
 */
public class DefaultMonitoredEntityContext extends SessionInitializationEventHandlerAdapter implements MonitoredEntityContext {

    public static final String OPERATION_PROPERTY = "hist_operation";
    public static final String DATE_PROPERTY = "hist_date";
    public static final String SQL_NOTE = "note";
    public static final String SQL_FIND_ALL = "findAll";
    public static final String SQL_FIND_BY_VERSION = "findByVersion";
    public static final String SQL_FIND_BY_OPERATION = "findByOperation";
    public static final String SQL_FIND_BEFORE_DATE = "findBeforeDate";
    public static final String SQL_FIND_AFTER_DATE = "findAfterDate";
    public static final String SQL_FIND_BEFORE_VERSION = "findBeforeVersion";
    public static final String SQL_FIND_AFTER_VERSION = "findAfterVersion";
    public static final String SQL_FIND_BETWEEN_VERSIONS = "findBetweenVersions";
    public static final String SQL_FIND_BETWEEN_DATES = "findBetweenDates";
    public static final String HISTORY_TABLE_NAME_PREFIX = "hist_";
    private final Collection<Class<?>> monitoredEntities = new CopyOnWriteArrayList<Class<?>>();
    private final TableMetadataRegistry tableMetadataRegistry = new DefaultTableMetadataRegistry();
    private final StatementRegistry statementRegistry = new DefaultStatementRegistry();
    private final Registry<StatementBuilder> statementBuilderRegistry = new ConcurrentRegistry<StatementBuilder>();
    private final RowHandler rowHandler = new DefaultRowHandler();
    private DataAccessSession session;
    private EntityHandlerContext entityHandlerContext;
    private EntityContext entityContext;
    private DataAccess dataAccess;

    @Override
    public void afterRegisteringEntities(EntityDefinitionContext entityDefinitionContext, Collection<Class> entityClasses) {
        /**
         * After all the entities have been registered with the active session, we can scan the entities for @Monitored
         */
        for (Class entityClass : entityClasses) {
            if (entityClass.isAnnotationPresent(Monitored.class)) {
                monitoredEntities.add(entityClass);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void afterPreparingStatements(StatementRegistryPreparator preparator, TableMetadataRegistry tableMetadataRegistry, StatementRegistry statementRegistry) {
        /**
         * We now have to create table metadata for the history of each monitored entity
         */
        for (Class<?> entity : monitoredEntities) {
            final TableMetadata<Object> entityTable = (TableMetadata<Object>) DatabaseUtils.copyTable(tableMetadataRegistry.getTableMetadata(entity));
            if (!entityTable.hasPrimaryKey()) {
                throw new IllegalStateException("@Monitored entity cannot be defined without a primary key: " + entityTable.getEntityType().getCanonicalName());
            }
            if (entityTable.getVersionColumn() == null) {
                throw new IllegalStateException("@Monitored entity cannot be defined without a version column: " + entityTable.getEntityType().getCanonicalName());
            }
            final ArrayList<ColumnMetadata> columns = new ArrayList<ColumnMetadata>();
            final String tableName = DatabaseUtils.unify(HISTORY_TABLE_NAME_PREFIX + entityTable.getName());
            final UnresolvedTableMetadata<Object> unresolvedTableMetadata = new UnresolvedTableMetadata<Object>((Class<Object>) entity);
            /**
             * We copy every column, losing all relation metadata
             */
            for (ColumnMetadata columnMetadata : entityTable.getColumns()) {
                /**
                 * the column's type-specific metadata must be copied from the foreign reference if this is actually a foreign key
                 */
                final int type = columnMetadata.getForeignReference() != null ? columnMetadata.getForeignReference().getType() : columnMetadata.getType();
                final int length = columnMetadata.getForeignReference() != null ? columnMetadata.getForeignReference().getLength() : columnMetadata.getLength();
                final int precision = columnMetadata.getForeignReference() != null ? columnMetadata.getForeignReference().getPrecision() : columnMetadata.getPrecision();
                final int scale = columnMetadata.getForeignReference() != null ? columnMetadata.getForeignReference().getScale() : columnMetadata.getScale();
                columns.add(new ResolvedColumnMetadata(unresolvedTableMetadata, columnMetadata.getDeclaringClass(), columnMetadata.getName(), type, columnMetadata.getPropertyName(), columnMetadata.getPropertyType(), columnMetadata.isNullable(), length, precision, scale, null, null, null, columnMetadata.isCollection(), columnMetadata.isComplex()));
            }
            /**
             * Now let's add the history-specific columns, as well as the version column. The version column is the mirror column from the
             * original version column.
             */
            columns.add(new ResolvedColumnMetadata(unresolvedTableMetadata, Object.class, "hist_operation", Types.VARCHAR, OPERATION_PROPERTY, OperationType.class, false, 255, 0, 0, false, false));
            columns.add(new ResolvedColumnMetadata(unresolvedTableMetadata, Object.class, "hist_date", Types.TIMESTAMP, DATE_PROPERTY, Date.class, false, 0, 0, 0, false, false));
            final ColumnMetadata versionColumn = with(columns).find(new ColumnNameFilter(entityTable.getVersionColumn().getName()));
            final TableMetadata<?> tableMetadata = new ResolvedTableMetadata<Object>(entityTable.getEntityType(), entityTable.getSchema(), tableName, Collections.<ConstraintMetadata>emptyList(), columns, Collections.<NamedQueryMetadata>emptyList(), Collections.<SequenceMetadata>emptyList(), Collections.<StoredProcedureMetadata>emptyList(), Collections.<RelationMetadata<Object, ?>>emptyList(), versionColumn, Collections.<OrderMetadata>emptyList());
            /**
             * The table metadata that has been figured out so far must be kept locally so as to be accessible by the context
             */
            this.tableMetadataRegistry.addTableMetadata(tableMetadata);
        }
    }

    @Override
    public void beforeSignalingInitialization(ConfigurableListableBeanFactory beanFactory, DataAccessSession session, DataStructureHandler dataStructureHandler) {
        /**
         * Before signaling to the data access that the session is ready, we need to register all statements that are
         * relevant to working with the history API.
         */
        final List<String> statements = Arrays.asList(SQL_FIND_ALL, SQL_FIND_BY_VERSION, SQL_FIND_BY_OPERATION, SQL_FIND_BEFORE_DATE, SQL_FIND_AFTER_DATE, SQL_FIND_BEFORE_VERSION, SQL_FIND_AFTER_VERSION, SQL_FIND_BETWEEN_VERSIONS, SQL_FIND_BETWEEN_DATES);
        final DatabaseDialect dialect = session.getDatabaseDialect();
        final Configuration configuration = FreemarkerUtils.getConfiguration(getClass(), "/monitoring/sql/standard");
        final Configuration localConfiguration = new Configuration();
        final FreemarkerStatementBuilder statementBuilder = new FreemarkerStatementBuilder(localConfiguration, "sql", dialect);
        try {
            for (String statement : statements) {
                statementBuilderRegistry.register(statement, new FreemarkerStatementBuilder(configuration, statement + ".sql.ftl", dialect));
            }
        } catch (RegistryException e) {
            throw new IllegalStateException("Failed to prepare statements builder context", e);
        }
        /**
         * Now, let's register the prepared statement builders with the entities
         */
        for (Class<?> monitoredEntity : monitoredEntities) {
            try {
                final TableMetadata<?> tableMetadata = tableMetadataRegistry.getTableMetadata(monitoredEntity);
                final ColumnMetadata primaryKey = with(tableMetadata.getColumns()).find(new ColumnNameFilter(session.getTableMetadataRegistry().getTableMetadata(monitoredEntity).getPrimaryKey().getColumns().iterator().next().getName()));
                final String prefix = monitoredEntity.getCanonicalName() + ".";
                for (String statement : statements) {
                    statementRegistry.register(prefix + statement, statementBuilderRegistry.get(statement).getStatement(tableMetadata, primaryKey));
                }
                statementRegistry.register(prefix + SQL_NOTE, dialect.getStatementBuilderContext().getManipulationStatementBuilder(Statements.Manipulation.INSERT).getStatement(tableMetadata));
                final List<MonitoringQuery> queries = new ArrayList<MonitoringQuery>();
                if (monitoredEntity.isAnnotationPresent(MonitoringQuery.class)) {
                    final MonitoringQuery annotation = monitoredEntity.getAnnotation(MonitoringQuery.class);
                    queries.add(annotation);
                } else if (monitoredEntity.isAnnotationPresent(MonitoringQueries.class)) {
                    Collections.addAll(queries, monitoredEntity.getAnnotation(MonitoringQueries.class).value());
                }
                /**
                 * To register named queries, we will need to create a dummy statement builder aware of the Freemarker
                 * conventions set for the framework and then register those statements
                 */
                for (MonitoringQuery query : queries) {
                    final StringTemplateLoader loader = new StringTemplateLoader();
                    loader.putTemplate("sql", query.query());
                    localConfiguration.setTemplateLoader(loader);
                    statementRegistry.register(prefix + query.name(), statementBuilder.getStatement(tableMetadata, primaryKey));
                }
            } catch (RegistryException e) {
                throw new IllegalStateException("Failed to prepare statements for the monitored entity " + monitoredEntity.getCanonicalName(), e);
            }
        }
        /**
         * Now, it is time to initialize the data structure for the history tables
         */
        dataStructureHandler.initialize(tableMetadataRegistry.getTables());
    }

    private PreparedStatement getStatement(Connection connection, Class<?> entityType, String queryName, Map<String, Object> values) {
        values = MapTools.prefixKeys(values, "value.");
        String prefix = "";
        for (Class<?> entity : monitoredEntities) {
            if (entity.isAssignableFrom(entityType)) {
                prefix = entity.getCanonicalName();
            }
        }
        final LocalStatementRegistry statementRegistry = new LocalStatementRegistry(this.statementRegistry, prefix);
        try {
            return statementRegistry.get(queryName).prepare(connection, new DefaultEntityMapCreator(), values);
        } catch (RegistryException e) {
            throw new UnsupportedStatementTypeError(queryName);
        }
    }

    @Override
    public List<Map<String, Object>> executeQuery(Class<?> entityType, String queryName, Map<String, Object> values) {
        final ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            final PreparedStatement preparedStatement = getStatement(session.getConnection(), entityType, queryName, values);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(rowHandler.handleRow(resultSet));
            }
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to execute the query " + queryName, e);
        }
        return result;
    }

    @Override
    public void executeUpdate(Class<?> entityType, String queryName, Map<String, Object> values) {
        try {
            getStatement(session.getConnection(), entityType, queryName, values).executeUpdate();
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to execute the query " + queryName, e);
        }
    }

    @Override
    public List<Map<String, Object>> executeQuery(Class<?> entityType, String queryName, Object value) {
        return executeQuery(entityType, queryName, QueryBuilderTools.unwrap(value));
    }

    @Override
    public void executeUpdate(Class<?> entityType, String queryName, Object value) {
        executeUpdate(entityType, queryName, QueryBuilderTools.unwrap(value));
    }

    @Override
    public <E, K extends Serializable> List<E> executeQuery(Class<E> entityType, String queryName, History<E, K> history) {
        final List<Map<String, Object>> maps = executeQuery(entityType, queryName, (Object) history);
        final ArrayList<E> list = new ArrayList<E>();
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entityType);
        final TableMetadata<E> tableMetadata = session.getTableMetadataRegistry().getTableMetadata(entityType);
        /**
         * Let's resolve foreign references
         */
        for (Map<String, Object> map : maps) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                final ColumnMetadata columnMetadata = with(tableMetadata.getColumns()).find(new ColumnNameFilter(entry.getKey()));
                if (columnMetadata == null) {
                    continue;
                }
                if (columnMetadata.getForeignReference() == null) {
                    continue;
                }
                /*
                 * Note: this will NOT replace the references to this entity in foreign entity. If for instance the foreign entity has a cascading
                 * OneToMany wherein the Many side is this one, references to the current version will not be replaced for replaces of this version
                 */
                map.put(entry.getKey(), dataAccess.find(columnMetadata.getForeignReference().getTable().getEntityType(), (Serializable) entry.getValue()));
            }
            list.add(entityHandler.fromMap(entityContext.getInstance(entityType), map));
        }
        return list;
    }

    @Override
    public <E, K extends Serializable> void executeUpdate(Class<E> entityType, String queryName, History<E, K> history) {
        executeUpdate(entityType, queryName, (Object) history);
    }

    @Override
    public <E, K extends Serializable> List<E> findAll(Class<E> entityType, K key) {
        return executeQuery(entityType, SQL_FIND_ALL, new History<E, K>(key));
    }

    @Override
    public <E, K extends Serializable> List<E> findBefore(Class<E> entityType, K key, Date date) {
        return executeQuery(entityType, SQL_FIND_BEFORE_DATE, new History<E, K>(key).setDate(date));
    }

    @Override
    public <E, K extends Serializable> List<E> findAfter(Class<E> entityType, K key, Date date) {
        return executeQuery(entityType, SQL_FIND_AFTER_DATE, new History<E, K>(key).setDate(date));
    }

    @Override
    public <E, K extends Serializable> List<E> findBefore(Class<E> entityType, K key, Serializable version) {
        return executeQuery(entityType, SQL_FIND_BEFORE_VERSION, new History<E, K>(key).setVersion(version));
    }

    @Override
    public <E, K extends Serializable> List<E> findAfter(Class<E> entityType, K key, Serializable version) {
        return executeQuery(entityType, SQL_FIND_AFTER_VERSION, new History<E, K>(key).setVersion(version));
    }

    @Override
    public <E, K extends Serializable> List<E> findBetween(Class<E> entityType, K key, Serializable from, Serializable to) {
        return executeQuery(entityType, SQL_FIND_BETWEEN_VERSIONS, new History<E, K>(key).setFromVersion(from).setToVersion(to));
    }

    @Override
    public <E, K extends Serializable> List<E> findBetween(Class<E> entityType, K key, Date from, Date to) {
        return executeQuery(entityType, SQL_FIND_BETWEEN_DATES, new History<E, K>(key).setFromDate(from).setToDate(to));
    }

    @Override
    public <E, K extends Serializable> List<E> findByOperation(Class<E> entityType, K key, OperationType operationType) {
        return executeQuery(entityType, SQL_FIND_BY_OPERATION, new History<E, K>(key).setOperation(operationType));
    }

    @Override
    public <E, K extends Serializable> E find(Class<E> entityType, K key, Serializable version) {
        final List<E> list = executeQuery(entityType, SQL_FIND_BY_VERSION, new History<E, K>(key).setVersion(version));
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new VersionColumnDefinitionError("There is more than one version of this entity");
        }
        return list.get(0);
    }

    @Override
    public <E, K extends Serializable> E revert(Class<E> entityType, K key, Serializable version) {
        final E oldEntity = find(entityType, key, version);
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entityType);
        final E currentEntity = dataAccess.find(entityType, key);
        final Serializable entityVersion = entityHandler.getVersion(currentEntity);
        entityHandler.copy(oldEntity, currentEntity);
        entityHandler.setVersion(currentEntity, entityVersion);
        return dataAccess.save(currentEntity);
    }

    @Override
    public <E> List<E> findAll(E sample) {
        final EntityHandler<E> handler = entityHandlerContext.getHandler(sample);
        return findAll(handler.getEntityType(), handler.getKey(sample));
    }

    @Override
    public <E> List<E> findBefore(E sample, Date date) {
        final EntityHandler<E> handler = entityHandlerContext.getHandler(sample);
        return findBefore(handler.getEntityType(), handler.getKey(sample), date);
    }

    @Override
    public <E> List<E> findAfter(E sample, Date date) {
        final EntityHandler<E> handler = entityHandlerContext.getHandler(sample);
        return findAfter(handler.getEntityType(), handler.getKey(sample), date);
    }

    @Override
    public <E> List<E> findBefore(E sample, Serializable version) {
        final EntityHandler<E> handler = entityHandlerContext.getHandler(sample);
        return findBefore(handler.getEntityType(), handler.getKey(sample), version);
    }

    @Override
    public <E> List<E> findAfter(E sample, Serializable version) {
        final EntityHandler<E> handler = entityHandlerContext.getHandler(sample);
        return findAfter(handler.getEntityType(), handler.getKey(sample), version);
    }

    @Override
    public <E> List<E> findBetween(E sample, Serializable from, Serializable to) {
        final EntityHandler<E> handler = entityHandlerContext.getHandler(sample);
        return findBetween(handler.getEntityType(), handler.getKey(sample), from, to);
    }

    @Override
    public <E> List<E> findBetween(E sample, Date from, Date to) {
        final EntityHandler<E> handler = entityHandlerContext.getHandler(sample);
        return findBetween(handler.getEntityType(), handler.getKey(sample), from, to);
    }

    @Override
    public <E> List<E> findByOperation(E sample, OperationType operationType) {
        final EntityHandler<E> handler = entityHandlerContext.getHandler(sample);
        return findByOperation(handler.getEntityType(), handler.getKey(sample), operationType);
    }

    @Override
    public <E> E find(E sample, Serializable version) {
        final EntityHandler<E> handler = entityHandlerContext.getHandler(sample);
        return find(handler.getEntityType(), handler.getKey(sample), version);
    }

    @Override
    public <E> E revert(E sample, Serializable version) {
        final EntityHandler<E> handler = entityHandlerContext.getHandler(sample);
        return revert(handler.getEntityType(), handler.getKey(sample), version);
    }

    @Override
    public boolean hasHistory(Object entity) {
        for (Class<?> monitoredEntity : monitoredEntities) {
            if (monitoredEntity.isInstance(entity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <E> void note(OperationType operationType, E entity) {
        final Map<String, Object> values = new HashMap<String, Object>(entityHandlerContext.toMap(session.getTableMetadataRegistry().getTableMetadata(entityHandlerContext.getHandler(entity).getEntityType()), entity));
        values.put(OPERATION_PROPERTY, operationType);
        values.put(DATE_PROPERTY, new Date());
        executeUpdate(entity.getClass(), SQL_NOTE, values);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        entityHandlerContext = applicationContext.getBean(EntityHandlerContext.class);
        entityContext = applicationContext.getBean(EntityContext.class);
        dataAccess = applicationContext.getBean(DataAccess.class);
        session = applicationContext.getBean(DataAccessSession.class);
        /**
         * We need to locate all beans that require access to this context and inject them with the context
         */
        final Collection<MonitoredEntityContextAware> beans = applicationContext.getBeansOfType(MonitoredEntityContextAware.class, false, true).values();
        for (MonitoredEntityContextAware bean : beans) {
            bean.setMonitoredEntityContext(this);
        }
    }
}
