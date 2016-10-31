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

package com.agileapes.dragonfly.data.impl;

import com.agileapes.dragonfly.data.FluentDataAccess;
import com.mmnaseri.couteau.basics.api.Cache;
import com.mmnaseri.couteau.basics.api.Filter;
import com.mmnaseri.couteau.basics.api.Processor;
import com.mmnaseri.couteau.basics.api.Transformer;
import com.mmnaseri.couteau.basics.api.impl.EqualityFilter;
import com.mmnaseri.couteau.basics.api.impl.SimpleDataDispenser;
import com.mmnaseri.couteau.context.error.RegistryException;
import com.mmnaseri.couteau.reflection.beans.BeanInitializer;
import com.mmnaseri.couteau.reflection.beans.BeanWrapper;
import com.mmnaseri.couteau.reflection.beans.impl.ConstructorBeanInitializer;
import com.mmnaseri.couteau.reflection.beans.impl.MethodBeanWrapper;
import com.mmnaseri.couteau.reflection.error.BeanInstantiationException;
import com.mmnaseri.couteau.reflection.util.ReflectionUtils;
import com.agileapes.dragonfly.annotations.ParameterMode;
import com.agileapes.dragonfly.annotations.Partial;
import com.agileapes.dragonfly.data.*;
import com.agileapes.dragonfly.entity.*;
import com.agileapes.dragonfly.entity.impl.*;
import com.agileapes.dragonfly.error.*;
import com.agileapes.dragonfly.events.DataAccessEventHandler;
import com.agileapes.dragonfly.events.EventHandlerContext;
import com.agileapes.dragonfly.events.impl.CompositeDataAccessEventHandler;
import com.agileapes.dragonfly.fluent.SelectQueryInitiator;
import com.agileapes.dragonfly.fluent.impl.DefaultSelectQueryInitiator;
import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.metadata.impl.ColumnMappingMetadataCollector;
import com.agileapes.dragonfly.metadata.impl.DefaultPagedResultOrderMetadata;
import com.agileapes.dragonfly.statement.Statement;
import com.agileapes.dragonfly.statement.*;
import com.agileapes.dragonfly.statement.impl.DefaultStatementPreparator;
import com.agileapes.dragonfly.statement.impl.DelegatingPreparedStatement;
import com.agileapes.dragonfly.statement.impl.FreemarkerSecondPassStatementBuilder;
import com.agileapes.dragonfly.statement.impl.ProcedureCallStatement;
import com.agileapes.dragonfly.tools.ColumnNameFilter;
import com.agileapes.dragonfly.tools.MapTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * <p>This class is the default implementation of the {@link DataAccess} interface.</p>
 *
 * <p>Some of the capabilities of the current implementation include:</p>
 *
 * <ul>
 *     <li>Highly traceable logging.</li>
 *     <li>Stacking batch operations of the same type together.</li>
 *     <li>Deferring deduction of the keys of inserted entities in batch operations.</li>
 *     <li>Connection piggy-backing for statements executed within the same thread.</li>
 *     <li>Support for complex event handler and extension points.</li>
 *     <li>Caching of delete statements for many-to-many relations within each thread.</li>
 *     <li>Freezing the element count view during batch operations until batch operations
 *     are successfully committed.</li>
 *     <li>Caching of initialized entities, until their persistent properties are modified
 *     externally.</li>
 *     <li>Support for operations on partial entities.</li>
 * </ul>
 *
 * <p><strong>NB</strong> This implementation of the data access interface does not provide
 * security measures. To use a secured version, you should instantiate {@link SecuredDataAccess},
 * instead.</p>
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/20, 23:29)
 */
public class DefaultDataAccess implements PartialDataAccess, EventHandlerContext, FluentDataAccess {

    private static final Log log = LogFactory.getLog(DataAccess.class);
    private static final Map<Statements.Manipulation, String> STATEMENTS = new ConcurrentHashMap<Statements.Manipulation, String>();
    private static final long SESSION_INITIALIZATION_TIMEOUT = 5000L;

    static {
        STATEMENTS.put(Statements.Manipulation.CALL, "call");
        STATEMENTS.put(Statements.Manipulation.DELETE_ALL, "deleteAll");
        STATEMENTS.put(Statements.Manipulation.DELETE_LIKE, "deleteLike");
        STATEMENTS.put(Statements.Manipulation.DELETE_ONE, "deleteByKey");
        STATEMENTS.put(Statements.Manipulation.DELETE_DEPENDENCIES, "deleteDependencies");
        STATEMENTS.put(Statements.Manipulation.DELETE_DEPENDENTS, "deleteDependents");
        STATEMENTS.put(Statements.Manipulation.FIND_ALL, "findAll");
        STATEMENTS.put(Statements.Manipulation.FIND_LIKE, "findLike");
        STATEMENTS.put(Statements.Manipulation.FIND_ONE, "findByKey");
        STATEMENTS.put(Statements.Manipulation.COUNT_ALL, "countAll");
        STATEMENTS.put(Statements.Manipulation.COUNT_LIKE, "countLike");
        STATEMENTS.put(Statements.Manipulation.COUNT_ONE, "countByKey");
        STATEMENTS.put(Statements.Manipulation.INSERT, "insert");
        STATEMENTS.put(Statements.Manipulation.TRUNCATE, "truncate");
        STATEMENTS.put(Statements.Manipulation.UPDATE, "updateBySample");
    }

    private final DataAccessSession session;
    private final EntityContext entityContext;
    private final EntityHandlerContext entityHandlerContext;
    private final BeanInitializer beanInitializer;
    private final ColumnMappingMetadataCollector metadataCollector;
    private final CompositeDataAccessEventHandler eventHandler;
    private final EntityInitializationContext initializationContext;
    private final RowHandler rowHandler;
    private final ThreadLocal<Map<Object, Object>> saveQueue;
    private final ThreadLocal<Set<Object>> deferredSaveQueue;
    private final ThreadLocal<Long> saveQueueLock;
    private final ThreadLocal<Set<Object>> deleteQueue;
    private final EntityMapCreator mapCreator;
    private final MapEntityCreator entityCreator;
    private final Map<Class<?>, Collection<ColumnMetadata>> partialEntityColumns = new ConcurrentHashMap<Class<?>, Collection<ColumnMetadata>>();
    private final ThreadLocal<Map<Class<?>, Map<Statements.Manipulation, Set<Statement>>>> deleteAllStatements;
    private final StatementPreparator statementPreparator;
    private final ThreadLocal<List<BatchOperationDescriptor>> batchOperation;
    private final ThreadLocal<List<Object>> deferredKeys;
    private final ThreadLocal<Boolean> batch;
    private final ThreadLocal<Set<LocalOperationResult>> localCounts;
    private final ThreadLocal<Stack<PreparedStatement>> localStatements;

    public DefaultDataAccess(DataAccessSession session, EntityContext entityContext, EntityHandlerContext entityHandlerContext, boolean autoInitialize) {
        this.session = session;
        this.entityContext = entityContext;
        this.entityHandlerContext = entityHandlerContext;
        this.beanInitializer = new ConstructorBeanInitializer();
        this.metadataCollector = new ColumnMappingMetadataCollector();
        this.eventHandler = new CompositeDataAccessEventHandler();
        this.initializationContext = new ThreadLocalEntityInitializationContext(this);
        this.rowHandler = new DefaultRowHandler();
        this.mapCreator = new DefaultEntityMapCreator();
        try {
            this.entityCreator = new DefaultMapEntityCreator();
        } catch (RegistryException e) {
            throw new DataAccessSessionInitializationError("Failed to initialize the map-to-entity converter", e);
        }
        this.saveQueue = new ThreadLocal<Map<Object, Object>>() {
            @Override
            protected Map<Object, Object> initialValue() {
                return new HashMap<Object, Object>();
            }
        };
        this.deleteQueue = new ThreadLocal<Set<Object>>() {
            @Override
            protected Set<Object> initialValue() {
                return new HashSet<Object>();
            }
        };
        this.deleteAllStatements = new ThreadLocal<Map<Class<?>, Map<Statements.Manipulation, Set<Statement>>>>() {
            @Override
            protected Map<Class<?>, Map<Statements.Manipulation, Set<Statement>>> initialValue() {
                return new HashMap<Class<?>, Map<Statements.Manipulation, Set<Statement>>>();
            }
        };
        this.batchOperation = new ThreadLocal<List<BatchOperationDescriptor>>();
        this.batch = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return false;
            }
        };
        this.entityContext.initialize(this);
        this.statementPreparator = new DefaultStatementPreparator(false);
        this.deferredKeys = new ThreadLocal<List<Object>>() {
            @Override
            protected List<Object> initialValue() {
                return new ArrayList<Object>();
            }
        };
        this.deferredSaveQueue = new ThreadLocal<Set<Object>>() {
            @Override
            protected Set<Object> initialValue() {
                return new HashSet<Object>();
            }
        };
        this.saveQueueLock = new ThreadLocal<Long>() {
            @Override
            protected Long initialValue() {
                return 0L;
            }
        };
        this.localCounts = new ThreadLocal<Set<LocalOperationResult>>() {
            @Override
            protected Set<LocalOperationResult> initialValue() {
                return new HashSet<LocalOperationResult>();
            }
        };
        this.localStatements = new ThreadLocal<Stack<PreparedStatement>>(){
            @Override
            protected Stack<PreparedStatement> initialValue() {
                return new Stack<PreparedStatement>();
            }
        };
        if (autoInitialize) {
            log.info("Automatically initializing the session");
            synchronized (this.session) {
                if (!this.session.isInitialized()) {
                    this.session.initialize();
                    this.session.markInitialized();
                }
            }
        }
    }

    /**
     * Connection handling
     */

    private Connection openConnection() {
        if (localStatements.get().isEmpty()) {
            return session.getConnection();
        } else {
            try {
                return localStatements.get().peek().getConnection();
            } catch (SQLException e) {
                throw new DatabaseConnectionError(e);
            }
        }
    }

    private void closeConnection(Connection connection) throws SQLException {
        if (localStatements.get().isEmpty()) {
            connection.close();
        }
    }

    private <S extends PreparedStatement> S openStatement(S preparedStatement) {
        localStatements.get().push(preparedStatement);
        return preparedStatement;
    }

    private void closeStatement(PreparedStatement preparedStatement) throws SQLException {
        localStatements.get().pop();
        preparedStatement.close();
    }


    /**
     * Internal update methods
     * These are helpers for the rest of the interface
     */

    private int getUpdateCount(PreparedStatement preparedStatement) {
        try {
            return preparedStatement.getUpdateCount();
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to retrieve the number of affected rows", e);
        }
    }

    private PreparedStatement internalExecuteUpdate(Class<?> entityType, Statements.Manipulation statementName) {
        return internalExecuteUpdate(entityType, statementName, Collections.<String, Object>emptyMap());
    }

    private PreparedStatement internalExecuteUpdate(Class<?> entityType, Statements.Manipulation statement, Map<String, Object> values) {
        return internalExecuteUpdate(getStatement(entityType, statement, null, StatementType.INSERT, StatementType.DELETE, StatementType.UPDATE, StatementType.TRUNCATE), values);
    }

    private PreparedStatement internalExecuteUpdate(Class<?> entityType, String statement, Map<String, Object> values) {
        return internalExecuteUpdate(getStatement(entityType, statement, null, StatementType.INSERT, StatementType.DELETE, StatementType.UPDATE, StatementType.TRUNCATE), values);
    }

    private PreparedStatement internalExecuteUpdate(Statement statement, Map<String, Object> values) {
        waitForSessionInitialization();
        if (isInBatchMode()) {
            if (batchOperation.get() == null) {
                batchOperation.set(new Stack<BatchOperationDescriptor>());
            }
            final List<BatchOperationDescriptor> operationDescriptors = batchOperation.get();
            boolean firstStep = operationDescriptors.isEmpty();
            if (!firstStep) {
                String sql = statement.getSql();
                if (statement.isDynamic()) {
                    sql = new FreemarkerSecondPassStatementBuilder(statement, session.getDatabaseDialect(), values).getStatement(statement.getTableMetadata()).getSql();
                }
                firstStep = !sql.equals(operationDescriptors.get(operationDescriptors.size() - 1).getSql());
            }
            final PreparedStatement preparedStatement;
            if (!firstStep) {
                preparedStatement = operationDescriptors.get(operationDescriptors.size() - 1).getPreparedStatement();
                statementPreparator.prepare(preparedStatement, statement.getTableMetadata(), values, operationDescriptors.get(operationDescriptors.size() - 1).getSql());
            } else {
                final BatchOperationDescriptor operationDescriptor = getPreparedStatement(statement, values);
                operationDescriptors.add(operationDescriptor);
                preparedStatement = operationDescriptor.getPreparedStatement();
            }
            try {
                preparedStatement.addBatch();
            } catch (SQLException e) {
                throw new BatchOperationExecutionError("Failed to add batch operation", e);
            }
            return preparedStatement;
        } else {
            final PreparedStatement preparedStatement = getPreparedStatement(statement, values).getPreparedStatement();
            try {
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new UnsuccessfulOperationError("Failed to execute update", e);
            }
            return preparedStatement;
        }
    }

    private void waitForSessionInitialization() {
        long time = System.currentTimeMillis();
        while (!session.isInitialized()) {
            if (System.currentTimeMillis() - time > SESSION_INITIALIZATION_TIMEOUT) {
                throw new DataAccessSessionInitializationError("Session initialization timed out after " + (System.currentTimeMillis() - time) + " milliseconds");
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new DataAccessSessionInitializationError("Session initialization was interrupted", e);
            }
        }
    }

    private synchronized BatchOperationDescriptor getPreparedStatement(Statement statement, Map<String, Object> values) {
        final Connection connection = openConnection();
        if (isInBatchMode()) {
            try {
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                throw new BatchOperationExecutionError("Failed to disable auto-commit mode for the current connection", e);
            }
        }
        final Statement finalStatement;
        if (statement.isDynamic()) {
            finalStatement = new FreemarkerSecondPassStatementBuilder(statement, session.getDatabaseDialect(), values).getStatement(statement.getTableMetadata());
        } else {
            finalStatement = statement;
        }
        final PreparedStatement preparedStatement = openStatement(new DelegatingPreparedStatement(finalStatement.prepare(connection, null, values), connection));
        return new BatchOperationDescriptor(preparedStatement, finalStatement.getSql());
    }

    /**
     * Internal query methods
     */

    private <E> List<E> internalExecuteQuery(Class<E> entityType, Statements.Manipulation statement, ResultOrderMetadata ordering) {
        return internalExecuteQuery(entityType, statement, Collections.<String, Object>emptyMap(), ordering);
    }

    private <E> List<E> internalExecuteQuery(Class<E> entityType, Statements.Manipulation statement, Map<String, Object> values, ResultOrderMetadata ordering) {
        return internalExecuteQuery(entityType, STATEMENTS.get(statement), values, ordering);
    }

    private <E> List<E> internalExecuteQuery(Class<E> entityType, String statementName, Map<String, Object> values, ResultOrderMetadata ordering) {
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entityType);
        final List<Map<String, Object>> maps = internalExecuteUntypedQuery(entityType, statementName, values, ordering);
        final ArrayList<E> result = new ArrayList<E>();
        for (Map<String, Object> entityMap : maps) {
            final E instance = entityContext.getInstance(entityType);
            entityHandler.fromMap(instance, entityMap);
            if (entityHandler.hasKey()) {
                final Serializable key = entityHandler.getKey(instance);
                if (initializationContext.contains(entityType, key)) {
                    result.add(initializationContext.get(entityType, key));
                    continue;
                }
            }
            prepareEntity(instance, entityMap);
            result.add(instance);
        }
        return result;
    }

    private <E> List<Map<String, Object>> internalExecuteUntypedQuery(Class<E> entityType, Statements.Manipulation statement, Map<String, Object> values, ResultOrderMetadata ordering) {
        return internalExecuteUntypedQuery(entityType, STATEMENTS.get(statement), values, ordering);
    }

    private <E> List<Map<String, Object>> internalExecuteUntypedQuery(Class<E> entityType, String statementName, Map<String, Object> values, ResultOrderMetadata ordering) {
        if (isInBatchMode() && !statementName.startsWith("count")) {
            throw new BatchOperationInterruptedByReadError();
        }
        waitForSessionInitialization();
        final Statement statement = getStatement(entityType, statementName, ordering, StatementType.QUERY);
        final Connection connection = openConnection();
        final PreparedStatement preparedStatement = openStatement(statement.prepare(connection, null, values));
        final ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(rowHandler.handleRow(resultSet));
            }
            resultSet.close();
            closeStatement(preparedStatement);
            closeConnection(connection);
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to retrieve result set from the database", e);
        }
        return result;
    }

    private <E> long internalCount(Class<E> entityType, Statements.Manipulation statement, Map<String, Object> values) {
        final LocalOperationResult result = new LocalOperationResult(entityType, statement, values);
        if (isInBatchMode() && localCounts.get().contains(result)) {
            return (Long) with(localCounts.get()).find(new EqualityFilter<LocalOperationResult>(result)).getResult();
        }
        final List<Map<String, Object>> list = internalExecuteUntypedQuery(entityType, statement, values, null);
        if (list.size() != 1) {
            throw new UnsuccessfulOperationError("Failed to execute statement");
        }
        final Long value = (Long) list.get(0).get(with(list.get(0).keySet()).find(new Filter<String>() {
            @Override
            public boolean accepts(String item) {
                return session.getDatabaseDialect().getCountColumn().equalsIgnoreCase(item);
            }
        }));
        if (isInBatchMode()) {
            result.setResult(value);
            localCounts.get().add(result);
        }
        return value;
    }

    /**
     * Internal statement access methods
     */

    private Statement getStatement(Class<?> entityType, Statements.Manipulation statement, ResultOrderMetadata ordering, StatementType... expected) {
        return getStatement(entityType, STATEMENTS.get(statement), ordering, expected);
    }

    private Statement getStatement(Class<?> entityType, final String statementName, ResultOrderMetadata ordering, StatementType... expected) {
        Statement result;
        try {
            if (ordering == null) {
                result = session.getStatementRegistry(entityType).get(statementName);
            } else {
                final StatementBuilder statementBuilder = session.getDatabaseDialect().getStatementBuilderContext().getManipulationStatementBuilder(with(STATEMENTS.keySet()).find(new Filter<Statements.Manipulation>() {
                    @Override
                    public boolean accepts(Statements.Manipulation item) {
                        return STATEMENTS.get(item).equals(statementName);
                    }
                }));
                ordering = new DefaultPagedResultOrderMetadata(ordering);
                result = statementBuilder.getStatement(session.getTableMetadataRegistry().getTableMetadata(entityType), ordering);
            }
        } catch (RegistryException e) {
            throw new NoSuchQueryError(entityType, statementName);
        }
        if (expected.length > 0) {
            boolean found = false;
            for (StatementType statementType : expected) {
                if (statementType.equals(result.getType())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new InvalidStatementTypeError(result.getType());
            }
        }
        return result;
    }

    private <E> InitializedEntity<E> getInitializedEntity(E entity) {
        //noinspection unchecked
        return (InitializedEntity<E>) getEnhancedEntity(entity);
    }

    private <E> E getEnhancedEntity(E entity) {
        if (entityContext.has(entity)) {
            return entity;
        }
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entity);
        final E result = entityContext.getInstance(entityHandler.getEntityType());
        entityHandler.copy(entity, result);
        if (result instanceof DataAccessAware) {
            ((DataAccessAware) result).setDataAccess(this);
        }
        if (result instanceof DataAccessSessionAware) {
            ((DataAccessSessionAware) result).setDataAccessSession(session);
        }
        if (session instanceof DefaultDataAccessSession && result instanceof DataStructureHandlerAware) {
            ((DataStructureHandlerAware) result).setDataStructureHandler(((DefaultDataAccessSession) session).getDataStructureHandler());
        }
        return result;
    }

    private <E> void prepareEntity(final E entity, Map<String, Object> values) {
        final E enhancedEntity = getEnhancedEntity(entity);
        final InitializedEntity<E> initializedEntity = getInitializedEntity(enhancedEntity);
        initializedEntity.setOriginalCopy(enhancedEntity);
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entity);
        final EntityInitializationContext initializationContext;
        if (initializedEntity.getInitializationContext() != null) {
            initializationContext = initializedEntity.getInitializationContext();
        } else {
            initializationContext = new DefaultEntityInitializationContext(this, this.initializationContext);
            final Serializable key = entityHandler.hasKey() ? entityHandler.getKey(enhancedEntity) : null;
            if (key != null) {
                initializationContext.register(entityHandler.getEntityType(), key, enhancedEntity);
            }
            initializedEntity.setInitializationContext(initializationContext);
        }
        initializationContext.lock();
        initializedEntity.setMap(values);
        entityHandler.incrementVersion(enhancedEntity);
        entityHandler.loadEagerRelations(enhancedEntity, values, initializationContext);
        final TableMetadata<E> tableMetadata = session.getTableMetadataRegistry().getTableMetadata(entityHandler.getEntityType());
        final BeanWrapper<E> wrapper = new MethodBeanWrapper<E>(enhancedEntity);
        with(tableMetadata.getForeignReferences())
                .forThose(
                        new Filter<RelationMetadata<E, ?>>() {
                            @Override
                            public boolean accepts(RelationMetadata<E, ?> item) {
                                return !item.isLazy() && item.getType().equals(RelationType.MANY_TO_MANY);
                            }
                        },
                        new Processor<RelationMetadata<E, ?>>() {
                            @Override
                            public void process(RelationMetadata<E, ?> reference) {
                                final Connection connection = openConnection();
                                final TableMetadata<?> middleTable = reference.getForeignTable();
                                final ManyToManyActionHelper helper = new ManyToManyActionHelper(statementPreparator, connection, session.getDatabaseDialect().getStatementBuilderContext(), middleTable, tableMetadata, reference, entityContext);
                                final ManyToManyMiddleEntity middleEntity = new ManyToManyMiddleEntity();
                                final BeanWrapper<ManyToManyMiddleEntity> middleEntityWrapper = new MethodBeanWrapper<ManyToManyMiddleEntity>(middleEntity);
                                try {
                                    middleEntityWrapper.setPropertyValue(with(middleTable.getColumns()).find(new ColumnNameFilter(tableMetadata.getName())).getPropertyName(), enhancedEntity);
                                    final Collection<Object> collection = ReflectionUtils.getCollection(wrapper.getPropertyType(reference.getPropertyName()));
                                    collection.addAll(helper.find(middleEntity, new EntityPreparationCallback() {
                                        @Override
                                        public void prepare(Object entity, Map<String, Object> values) {
                                            prepareEntity(entity, values);
                                        }
                                    }, new Transformer<Object, Object>() {
                                        @Override
                                        public Object map(Object input) {
                                            final EntityHandler<Object> handler = entityHandlerContext.getHandler(input);
                                            if (handler.hasKey() && handler.getKey(input) != null && initializationContext.contains(handler.getEntityType(), handler.getKey(input))) {
                                                final Object cached;
                                                if (entityHandler.hasKey()) {
                                                    cached = initializationContext.get(handler.getEntityType(), handler.getKey(input), entityHandler.getEntityType(), entityHandler.getKey(entity));
                                                } else {
                                                    cached = initializationContext.get(handler.getEntityType(), handler.getKey(input));
                                                }
                                                return cached;
                                            }
                                            return null;
                                        }
                                    }));
                                    wrapper.setPropertyValue(reference.getPropertyName(), collection);
                                } catch (Exception e) {
                                    throw new EntityInitializationError(entityHandler.getEntityType(), e);
                                }
                                try {
                                    closeConnection(connection);
                                } catch (SQLException e) {
                                    throw new EntityInitializationError(entityHandler.getEntityType(), e);
                                }
                            }
                        }
                );
        initializationContext.unlock();
    }

    /**
     * Internal methods for handling partial data requests
     */

    private synchronized Collection<ColumnMetadata> getPartialEntityMetadata(Class<?> partialEntityType) {
        if (!partialEntityColumns.containsKey(partialEntityType)) {
            partialEntityColumns.put(partialEntityType, metadataCollector.collectMetadata(partialEntityType));
        }
        return partialEntityColumns.get(partialEntityType);
    }

    /**
     * Batch processing methods
     */

    private Boolean isInBatchMode() {
        return batch.get();
    }

    private synchronized void startBatch() {
        log.info("Starting batch operation");
        if (isInBatchMode()) {
            throw new BatchOperationAlreadyStartedError();
        }
        batch.set(true);
    }

    private synchronized List<Integer> endBatch() {
        if (!isInBatchMode()) {
            throw new NoBatchOperationError();
        }
        localCounts.get().clear();
        final List<BatchOperationDescriptor> descriptors = batchOperation.get();
        batchOperation.remove();
        batch.set(false);
        final ArrayList<Integer> result = new ArrayList<Integer>();
        if (descriptors == null) {
            return result;
        }
        log.info("There are " + descriptors.size() + " operation stack(s) to perform");
        while (!descriptors.isEmpty()) {
            final BatchOperationDescriptor descriptor = descriptors.get(0);
            descriptors.remove(0);
            final int[] batchResult;
            log.info("Executing batch operation for statement: " + descriptor.getSql());
            final PreparedStatement preparedStatement = descriptor.getPreparedStatement();
            final Connection connection;
            try {
                connection = preparedStatement.getConnection();
                long time = System.nanoTime();
                batchResult = preparedStatement.executeBatch();
                connection.commit();
                log.info(batchResult.length + " operation(s) completed successfully in " + (System.nanoTime() - time) + "ns");
            } catch (SQLException e) {
                throw new BatchOperationExecutionError("Failed to execute operation batch", e);
            }
            if (StatementType.getStatementType(descriptor.getSql()).equals(StatementType.INSERT)) {
                try {
                    final List<Object> deferredEntities = deferredKeys.get();
                    final ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    while (generatedKeys.next()) {
                        final Object entity = deferredEntities.get(0);
                        deferredEntities.remove(0);
                        final EntityHandler<Object> entityHandler = entityHandlerContext.getHandler(entity);
                        entityHandler.setKey(entity, session.getDatabaseDialect().retrieveKey(generatedKeys));
                    }
                } catch (SQLException e) {
                    throw new BatchOperationExecutionError("Failed to retrieve generated keys", e);
                }
            }
            for (int i : batchResult) {
                result.add(i);
            }
            cleanUpStatement(preparedStatement);
        }
        return result;
    }

    /**
     * Partial data access support
     */

    @Override
    public <O> List<O> executeQuery(O sample) {
        final Class<?> partialEntity = sample.getClass();
        //noinspection unchecked
        return (List<O>) executeQuery(partialEntity, mapCreator.toMap(getPartialEntityMetadata(partialEntity), sample));
    }

    @Override
    public <O> List<O> executeQuery(Class<O> resultType) {
        return executeQuery(resultType, Collections.<String, Object>emptyMap());
    }

    @Override
    public <O> List<O> executeQuery(Class<O> resultType, Map<String, Object> values) {
        if (!resultType.isAnnotationPresent(Partial.class)) {
            throw new PartialEntityDefinitionError("Expected to find @Partial on " + resultType.getCanonicalName());
        }
        final Partial annotation = resultType.getAnnotation(Partial.class);
        final List<Map<String, Object>> maps = executeUntypedQuery(annotation.targetEntity(), annotation.query(), values);
        final ArrayList<O> result = new ArrayList<O>();
        for (Map<String, Object> map : maps) {
            final O partialEntity;
            try {
                partialEntity = beanInitializer.initialize(resultType, new Class[0]);
            } catch (BeanInstantiationException e) {
                throw new EntityInitializationError(resultType, e);
            }
            result.add(entityCreator.fromMap(partialEntity, getPartialEntityMetadata(resultType), map));
        }
        return result;
    }

    @Override
    public <E> List<Map<String, Object>> executeUntypedQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        final ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        final Statement statement = getStatement(entityType, queryName, null, StatementType.QUERY);
        final Connection connection = openConnection();
        final PreparedStatement preparedStatement = openStatement(statement.prepare(connection, null, values));
        final ResultSet resultSet;
        try {
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to execute query " + entityType.getCanonicalName() + "." + queryName, e);
        }
        try {
            while (resultSet.next()) {
                result.add(rowHandler.handleRow(resultSet));
            }
            resultSet.close();
            closeStatement(preparedStatement);
            closeConnection(connection);
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to load result set", e);
        }
        return result;
    }

    @Override
    public <E, R> List<R> executeTypedQuery(Class<E> entityType, String queryName, Class<R> resultType, Map<String, Object> values) {
        final List<Map<String, Object>> maps = executeUntypedQuery(entityType, queryName, values);
        final ArrayList<R> list = new ArrayList<R>();
        for (Map<String, Object> map : maps) {
            if (map.size() > 1) {
                throw new QueryDefinitionError(entityType, queryName, "Query has more than one column to choose from");
            } else if (map.isEmpty()) {
                continue;
            }
            final Object value = map.values().iterator().next();
            if (resultType.isInstance(value)) {
                list.add(resultType.cast(value));
            } else {
                throw new QueryDefinitionError(entityType, queryName, "Expected value to be of type " + resultType.getCanonicalName() + " while it was " + value);
            }
        }
        return list;
    }

    @Override
    public <E> List<Object> executeTypedQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        return executeTypedQuery(entityType, queryName, Object.class, values);
    }

    @Override
    public <O> int executeUpdate(O sample) {
        final Class<?> resultType = sample.getClass();
        if (!resultType.isAnnotationPresent(Partial.class)) {
            throw new PartialEntityDefinitionError("Expected to find @Partial on " + resultType.getCanonicalName());
        }
        final Partial annotation = resultType.getAnnotation(Partial.class);
        final Statement statement = getStatement(annotation.targetEntity(), annotation.query(), null, StatementType.INSERT, StatementType.DELETE, StatementType.UPDATE);
        final PreparedStatement preparedStatement = openStatement(statement.prepare(openConnection(), mapCreator, sample));
        try {
            final int affectedRows = preparedStatement.executeUpdate();
            cleanUpStatement(preparedStatement);
            return affectedRows;
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to execute update", e);
        }
    }

    /**
     * Data access methods
     */

    @Override
    public <E> E save(E entity) {
        final Map<Object, Object> saveQueue = this.saveQueue.get();
        if (saveQueue.containsKey(entity)) {
            //noinspection unchecked
            return (E) saveQueue.get(entity);
        }
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entity);
        final E enhancedEntity = getEnhancedEntity(entity);
        saveQueueLock.set(saveQueueLock.get() + 1);
        if (!enhancedEntity.equals(entity)) {
            saveQueue.put(entity, enhancedEntity);
        }
        eventHandler.beforeSave(enhancedEntity);
        boolean shouldUpdate = entityHandler.hasKey() && entityHandler.isKeyAutoGenerated() && entityHandler.getKey(enhancedEntity) != null;
        if (!shouldUpdate) {
            insert(enhancedEntity);
        } else {
            update(enhancedEntity);
        }
        eventHandler.afterSave(enhancedEntity);
        entityHandler.copy(enhancedEntity, entity);
        saveQueueLock.set(saveQueueLock.get() - 1);
        if (saveQueueLock.get() == 0) {
            saveQueue.remove(entity);
            for (Object object : deferredSaveQueue.get()) {
                saveQueue.remove(object);
            }
            deferredSaveQueue.get().clear();
        } else {
            deferredSaveQueue.get().add(entity);
        }
        return enhancedEntity;
    }

    @Override
    public <E> E insert(E entity) {
        final Map<Object, Object> saveQueue = this.saveQueue.get();
        if (saveQueue.containsKey(entity)) {
            //noinspection unchecked
            return (E) saveQueue.get(entity);
        }
        saveQueueLock.set(saveQueueLock.get() + 1);
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entity);
        final E enhancedEntity = getEnhancedEntity(entity);
        saveQueue.put(entity, enhancedEntity);
        final InitializedEntity<E> initializedEntity = getInitializedEntity(enhancedEntity);
        initializedEntity.freeze();
        entityHandler.initializeVersion(enhancedEntity);
        final Map<String, Object> sequenceValues = new HashMap<String, Object>();
        sequenceValues.putAll(session.getDatabaseDialect().loadTableValues(session.getTableMetadataRegistry().getTableMetadata(TableKeyGeneratorEntity.class), session.getTableMetadataRegistry().getTableMetadata(entityHandler.getEntityType()), session));
        sequenceValues.putAll(session.getDatabaseDialect().loadSequenceValues(session.getTableMetadataRegistry().getTableMetadata(entityHandler.getEntityType())));
        entityHandler.fromMap(enhancedEntity, sequenceValues);
        entityHandler.saveDependencyRelations(enhancedEntity, this);
        eventHandler.beforeInsert(enhancedEntity);
        final PreparedStatement preparedStatement = internalExecuteUpdate(entityHandler.getEntityType(), Statements.Manipulation.INSERT, MapTools.prefixKeys(entityHandler.toMap(enhancedEntity), "value."));
        if (entityHandler.hasKey() && entityHandler.isKeyAutoGenerated()) {
            if (isInBatchMode()) {
                deferredKeys.get().add(enhancedEntity);
            } else {
                try {
                    final ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        entityHandler.setKey(enhancedEntity, session.getDatabaseDialect().retrieveKey(generatedKeys));
                    }
                    if (initializedEntity.getInitializationContext() == null) {
                        final DefaultEntityInitializationContext entityInitializationContext = new DefaultEntityInitializationContext(this, initializationContext);
                        entityInitializationContext.register(entityHandler.getEntityType(), entityHandler.getKey(enhancedEntity), enhancedEntity);
                        initializedEntity.setInitializationContext(entityInitializationContext);
                    }
                } catch (SQLException e) {
                    throw new UnsupportedOperationException("Failed to retrieve auto-generated keys", e);
                }
            }
        }
        entityHandler.incrementVersion(enhancedEntity);
        if (!isInBatchMode()) {
            cleanUpStatement(preparedStatement);
        }
        initializedEntity.setOriginalCopy(enhancedEntity);
        eventHandler.afterInsert(enhancedEntity);
        saveDependents(entityHandler, enhancedEntity);
        saveQueueLock.set(saveQueueLock.get() - 1);
        if (saveQueueLock.get() == 0) {
            saveQueue.remove(entity);
            for (Object object : deferredSaveQueue.get()) {
                saveQueue.remove(object);
            }
            deferredSaveQueue.get().clear();
        } else {
            deferredSaveQueue.get().add(entity);
        }
        entityHandler.copy(enhancedEntity, entity);
        initializedEntity.unfreeze();
        return enhancedEntity;
    }

    private <E> void saveDependents(final EntityHandler<E> entityHandler, E entity) {
        entityHandler.saveDependentRelations(entity, this, entityContext);
        final Map<TableMetadata<?>, Set<ManyToManyMiddleEntity>> relatedObjects = entityHandler.getManyToManyRelatedObjects(entity);
        final Connection connection = openConnection();
        try {
            final Cache<TableMetadata<?>, ManyToManyActionHelper> helpers = new SimpleDataDispenser<TableMetadata<?>, ManyToManyActionHelper>() {
                @Override
                protected ManyToManyActionHelper produce(TableMetadata<?> tableMetadata) {
                    return new ManyToManyActionHelper(statementPreparator, connection, session.getDatabaseDialect().getStatementBuilderContext(), tableMetadata, session.getTableMetadataRegistry().getTableMetadata(entityHandler.getEntityType()), null, entityContext);
                }
            };
            for (Map.Entry<TableMetadata<?>, Set<ManyToManyMiddleEntity>> entry : relatedObjects.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    continue;
                }
                final ManyToManyMiddleEntity someRelation = entry.getValue().iterator().next();
                final ManyToManyActionHelper helper = helpers.read(entry.getKey());
                helper.delete(someRelation);
                 if (entry.getValue().size() == 1 && !someRelation.isComplete()) {
                    helper.close();
                    continue;
                }
                for (ManyToManyMiddleEntity middleEntity : entry.getValue()) {
                    helper.insert(middleEntity);
                    helper.invalidate(middleEntity, initializationContext, entityHandlerContext);
                }
                helper.close();
            }
            closeConnection(connection);
        } catch (Exception e) {
            throw new UnsuccessfulOperationError("Failed to save dependent many-to-many objects", e);
        }
    }

    private void cleanUpStatement(PreparedStatement preparedStatement) {
        try {
            final Connection connection = preparedStatement.getConnection();
            closeStatement(preparedStatement);
            closeConnection(connection);
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to clean up", e);
        }
    }

    @Override
    public <E> E update(E entity) {
        final Map<Object, Object> saveQueue = this.saveQueue.get();
        if (saveQueue.containsKey(entity)) {
            //noinspection unchecked
            return (E) saveQueue.get(entity);
        }
        saveQueueLock.set(saveQueueLock.get() + 1);
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entity);
        final E enhancedEntity = getEnhancedEntity(entity);
        if (entityHandler.hasKey() && entityHandler.getKey(enhancedEntity) != null) {
            initializationContext.delete(entityHandler.getEntityType(), entityHandler.getKey(enhancedEntity));
        }
        saveQueue.put(entity, enhancedEntity);
        final InitializedEntity<E> initializedEntity = getInitializedEntity(enhancedEntity);
        entityHandler.saveDependencyRelations(enhancedEntity, this);
        eventHandler.beforeUpdate(enhancedEntity);
        final Map<String, Object> current = entityHandler.toMap(enhancedEntity);
        final Map<String, Object> values = new HashMap<String, Object>();
        values.putAll(MapTools.prefixKeys(current, "value."));
        values.putAll(MapTools.prefixKeys(current, "new."));
        if (initializedEntity.getOriginalCopy() == null) {
            initializedEntity.setOriginalCopy(enhancedEntity);
        }
        final E originalCopy = initializedEntity.getOriginalCopy();
        initializedEntity.freeze();
        final Map<String, Object> original = entityHandler.toMap(originalCopy);
        values.putAll(MapTools.prefixKeys(original, "old."));
        for (String key : original.keySet()) {
            if (!values.containsKey("value." + key)) {
                values.put("value." + key, original.get(key));
            }
        }
        final PreparedStatement preparedStatement = internalExecuteUpdate(entityHandler.getEntityType(), Statements.Manipulation.UPDATE, values);
        try {
            final boolean updated = preparedStatement.getUpdateCount() > 0;
            if (entityHandler.isLockable() && !updated) {
                throw new OptimisticLockingFailureError(entityHandler.getEntityType());
            }
            entityHandler.incrementVersion(enhancedEntity);
            eventHandler.afterUpdate(enhancedEntity, updated);
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to count the number of updated elements", e);
        }
        cleanUpStatement(preparedStatement);
        saveDependents(entityHandler, enhancedEntity);
        saveQueueLock.set(saveQueueLock.get() - 1);
        if (saveQueueLock.get() == 0) {
            saveQueue.remove(entity);
            for (Object object : deferredSaveQueue.get()) {
                saveQueue.remove(object);
            }
            deferredSaveQueue.get().clear();
        } else {
            deferredSaveQueue.get().add(entity);
        }
        entityHandler.copy(enhancedEntity, entity);
        if (entityHandler.hasKey() && entityHandler.getKey(enhancedEntity) != null) {
            initializationContext.register(entityHandler.getEntityType(), entityHandler.getKey(enhancedEntity), enhancedEntity);
        }
        initializedEntity.unfreeze();
        return enhancedEntity;
    }

    @Override
    public <E> void delete(E entity) {
        final Set<Object> deleteQueue = this.deleteQueue.get();
        if (deleteQueue.contains(entity)) {
            return;
        }
        deleteQueue.add(entity);
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entity);
        final E enhancedEntity = getEnhancedEntity(entity);
        final InitializedEntity<E> initializedEntity = getInitializedEntity(enhancedEntity);
        initializedEntity.freeze();
        eventHandler.beforeDelete(enhancedEntity);
        final Map<String, Object> map = MapTools.prefixKeys(entityHandler.toMap(enhancedEntity), "value.");
        if ((entityHandler.hasKey() && entityHandler.getKey(enhancedEntity) != null && exists(entityHandler.getEntityType(), entityHandler.getKey(enhancedEntity))) || exists(entityHandler.getEntityType(), map)) {
            deleteDependencies(entityHandler, enhancedEntity);
            final Statements.Manipulation statement;
            if (entityHandler.hasKey() && entityHandler.getKey(enhancedEntity) != null) {
                statement = Statements.Manipulation.DELETE_ONE;
            } else {
                statement = Statements.Manipulation.DELETE_LIKE;
            }
            cleanUpStatement(internalExecuteUpdate(entityHandler.getEntityType(), statement, map));
            entityHandler.deleteDependentRelations(enhancedEntity, this);
        }
        eventHandler.afterDelete(enhancedEntity);
        initializedEntity.unfreeze();
        deleteQueue.remove(entity);
    }

    private <E> void deleteDependencies(final EntityHandler<E> entityHandler, final E enhancedEntity) {
        entityHandler.deleteDependencyRelations(enhancedEntity, this);
        final TableMetadata<E> tableMetadata = session.getTableMetadataRegistry().getTableMetadata(entityHandler.getEntityType());
        final Connection connection = openConnection();
        with(tableMetadata.getForeignReferences())
                .forThose(
                        new Filter<RelationMetadata<E, ?>>() {
                            @Override
                            public boolean accepts(RelationMetadata<E, ?> item) {
                                return item.getCascadeMetadata().cascadeRemove() && RelationType.MANY_TO_MANY.equals(item.getType());
                            }
                        },
                        new Processor<RelationMetadata<E, ?>>() {
                            @Override
                            public void process(RelationMetadata<E, ?> reference) {
                                final TableMetadata<?> middleTable = reference.getForeignTable();
                                final ManyToManyActionHelper helper = new ManyToManyActionHelper(statementPreparator, connection, session.getDatabaseDialect().getStatementBuilderContext(), middleTable, tableMetadata, reference, entityContext);
                                final ManyToManyMiddleEntity middleEntity = new ManyToManyMiddleEntity();
                                final BeanWrapper<ManyToManyMiddleEntity> middleEntityWrapper = new MethodBeanWrapper<ManyToManyMiddleEntity>(middleEntity);
                                try {
                                    middleEntityWrapper.setPropertyValue(with(middleTable.getColumns()).find(new ColumnNameFilter(tableMetadata.getName())).getPropertyName(), enhancedEntity);
                                } catch (Exception e) {
                                    throw new EntityInitializationError(entityHandler.getEntityType(), e);
                                }
                                //delete foreign relations by cascading
                                final List<Object> found = helper.find(middleEntity, null, null);
                                helper.delete(middleEntity);
                                for (Object instance : found) {
                                    delete(instance);
                                }
                            }
                        }
                );
        try {
            closeConnection(connection);
        } catch (SQLException e) {
            throw new EntityInitializationError(entityHandler.getEntityType(), e);
        }
    }

    @Override
    public <E, K extends Serializable> void delete(Class<E> entityType, K key) {
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entityType);
        final E instance = entityContext.getInstance(entityType);
        entityHandler.setKey(instance, key);
        eventHandler.beforeDelete(entityType, key);
        delete(instance);
        initializationContext.delete(entityType, key);
        eventHandler.afterDelete(entityType, key);
    }

    @Override
    public <E> void deleteAll(final Class<E> entityType) {
        eventHandler.beforeDeleteAll(entityType);
        deleteDependents(entityType);
        cleanUpStatement(internalExecuteUpdate(entityType, Statements.Manipulation.DELETE_ALL));
        initializationContext.delete(entityType);
        deleteDependencies(entityType);
        eventHandler.afterDeleteAll(entityType);
    }

    private synchronized <E> void deleteDependents(Class<E> entityType) {
        prepareDeleteAllStatements(entityType, Statements.Manipulation.DELETE_DEPENDENTS);
        with(deleteAllStatements.get().get(entityType).get(Statements.Manipulation.DELETE_DEPENDENTS)).each(new Processor<Statement>() {
            @Override
            public void process(Statement statement) {
                cleanUpStatement(internalExecuteUpdate(statement, Collections.<String, Object>emptyMap()));
            }
        });
    }

    private synchronized <E> void deleteDependencies(Class<E> entityType) {
        prepareDeleteAllStatements(entityType, Statements.Manipulation.DELETE_DEPENDENCIES);
        with(deleteAllStatements.get().get(entityType).get(Statements.Manipulation.DELETE_DEPENDENCIES)).each(new Processor<Statement>() {
            @Override
            public void process(Statement statement) {
                internalExecuteUpdate(statement, Collections.<String, Object>emptyMap());
            }
        });
    }

    private <E> void prepareDeleteAllStatements(Class<E> entityType, final Statements.Manipulation statementType) {
        if (!deleteAllStatements.get().containsKey(entityType) || !deleteAllStatements.get().get(entityType).containsKey(statementType)) {
            final Map<Statements.Manipulation, Set<Statement>> map = deleteAllStatements.get().containsKey(entityType) ? deleteAllStatements.get().get(entityType) : new HashMap<Statements.Manipulation, Set<Statement>>();
            final Set<Statement> statements = map.containsKey(statementType) ? map.get(statementType) : new HashSet<Statement>();
            final TableMetadata<E> tableMetadata = session.getTableMetadataRegistry().getTableMetadata(entityType);
            with(tableMetadata.getForeignReferences())
                    .forThose(new Filter<RelationMetadata<E, ?>>() {
                                  @Override
                                  public boolean accepts(RelationMetadata<E, ?> relationMetadata) {
                                      return relationMetadata.getCascadeMetadata().cascadeRemove() && (statementType.equals(Statements.Manipulation.DELETE_DEPENDENCIES) ? relationMetadata.isOwner() : !relationMetadata.isOwner());
                                  }
                              },
                            new Processor<RelationMetadata<E, ?>>() {
                                @Override
                                public void process(RelationMetadata<E, ?> relationMetadata) {
                                    final StatementBuilder builder = session.getDatabaseDialect().getStatementBuilderContext().getManipulationStatementBuilder(statementType);
                                    statements.add(builder.getStatement(tableMetadata, relationMetadata));
                                }
                            }
                    );
            map.put(statementType, statements);
            deleteAllStatements.get().put(entityType, map);
        }
    }

    @Override
    public <E> void truncate(Class<E> entityType) {
        eventHandler.beforeTruncate(entityType);
        internalExecuteUpdate(entityType, Statements.Manipulation.TRUNCATE);
        initializationContext.delete(entityType);
        eventHandler.afterTruncate(entityType);
    }

    @Override
    public <E> List<E> find(E sample) {
        return find(sample, null);
    }

    @Override
    public <E> List<E> find(E sample, String order) {
        return find(sample, order, -1, -1);
    }

    @Override
    public <E> List<E> find(E sample, int pageSize, int pageNumber) {
        return find(sample, null, pageSize, pageNumber);
    }

    @Override
    public <E> List<E> find(E sample, String order, int pageSize, int pageNumber) {
        final E enhancedEntity = getEnhancedEntity(sample);
        final InitializedEntity<E> initializedEntity = getInitializedEntity(enhancedEntity);
        initializedEntity.freeze();
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(sample);
        ResultOrderMetadata ordering;
        if (order != null) {
            ordering = new OrderExpressionParser(session.getTableMetadataRegistry().getTableMetadata(entityHandler.getEntityType())).map(order);
        } else {
            ordering = null;
        }
        if (pageSize > 0 && pageNumber > 0) {
            if (ordering == null) {
                ordering = new DefaultPagedResultOrderMetadata(pageSize, pageNumber);
            } else {
                ordering = new DefaultPagedResultOrderMetadata(ordering, pageSize, pageNumber);
            }
        }
        eventHandler.beforeFind(enhancedEntity);
        final List<E> found = internalExecuteQuery(entityHandler.getEntityType(), Statements.Manipulation.FIND_LIKE, MapTools.prefixKeys(entityHandler.toMap(enhancedEntity), "value."), ordering);
        eventHandler.afterFind(enhancedEntity, found);
        initializedEntity.unfreeze();
        return found;
    }

    @Override
    public <E, K extends Serializable> E find(Class<E> entityType, K key) {
        if (initializationContext.contains(entityType, key)) {
            return initializationContext.get(entityType, key);
        }
        final E instance = entityContext.getInstance(entityType);
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entityType);
        entityHandler.setKey(instance, key);
        final Map<String, Object> map = MapTools.prefixKeys(entityHandler.toMap(instance), "value.");
        final List<E> list = internalExecuteQuery(entityType, Statements.Manipulation.FIND_ONE, map, null);
        final E result;
        if (list.isEmpty()) {
            result = null;
        } else if (list.size() == 1) {
            result = list.get(0);
        } else {
            throw new ObjectKeyError(entityType, key);
        }
        return eventHandler.afterFind(entityType, key, result);
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType) {
        return findAll(entityType, null);
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType, String order) {
        return findAll(entityType, order, -1, -1);
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType, String order, int pageSize, int pageNumber) {
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entityType);
        ResultOrderMetadata ordering;
        if (order != null) {
            ordering = new OrderExpressionParser(session.getTableMetadataRegistry().getTableMetadata(entityHandler.getEntityType())).map(order);
        } else {
            ordering = null;
        }
        if (pageSize > 0 && pageNumber > 0) {
            if (ordering == null) {
                ordering = new DefaultPagedResultOrderMetadata(pageSize, pageNumber);
            } else {
                ordering = new DefaultPagedResultOrderMetadata(ordering, pageSize, pageNumber);
            }
        }
        eventHandler.beforeFindAll(entityType);
        final List<E> found = internalExecuteQuery(entityHandler.getEntityType(), Statements.Manipulation.FIND_ALL, ordering);
        eventHandler.afterFindAll(entityType, found);
        return found;
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType, int pageSize, int pageNumber) {
        return findAll(entityType, null, pageSize, pageNumber);
    }

    @Override
    public <E> int executeUpdate(Class<E> entityType, String queryName, Map<String, Object> values) {
        eventHandler.beforeExecuteUpdate(entityType, queryName, values);
        final int affectedItems = getUpdateCount(internalExecuteUpdate(entityType, queryName, MapTools.prefixKeys(values, "value.")));
        eventHandler.afterExecuteUpdate(entityType, queryName, values, affectedItems);
        return affectedItems;
    }

    @Override
    public <E> int executeUpdate(E sample, String queryName) {
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(sample);
        final E enhancedEntity = getEnhancedEntity(sample);
        eventHandler.beforeExecuteUpdate(enhancedEntity, queryName);
        final int affectedItems = getUpdateCount(internalExecuteUpdate(entityHandler.getEntityType(), queryName, MapTools.prefixKeys(entityHandler.toMap(enhancedEntity), "value.")));
        eventHandler.afterExecuteUpdate(enhancedEntity, queryName, affectedItems);
        return affectedItems;
    }

    @Override
    public <E> List<E> executeQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        eventHandler.beforeExecuteQuery(entityType, queryName, values);
        final List<E> list = internalExecuteQuery(entityType, queryName, MapTools.prefixKeys(values, "value."), null);
        eventHandler.afterExecuteQuery(entityType, queryName, values, list);
        return list;
    }

    @Override
    public <E> List<E> executeQuery(E sample, String queryName) {
        final E enhancedEntity = getEnhancedEntity(sample);
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(enhancedEntity);
        eventHandler.beforeExecuteQuery(enhancedEntity, queryName);
        final List<E> list = internalExecuteQuery(entityHandler.getEntityType(), queryName, MapTools.prefixKeys(entityHandler.toMap(enhancedEntity), "value."), null);
        eventHandler.afterExecuteQuery(enhancedEntity, queryName, list);
        return list;
    }

    @Override
    public <E> List<?> call(Class<E> entityType, final String procedureName, Object... parameters) {
        if (isInBatchMode()) {
            throw new BatchOperationInterruptedByProcedureError();
        }
        log.info("Calling to stored procedure " + entityType.getCanonicalName() + "." + procedureName);
        final TableMetadata<E> tableMetadata = session.getTableMetadataRegistry().getTableMetadata(entityType);
        //noinspection unchecked
        final StoredProcedureMetadata procedureMetadata = with(tableMetadata.getProcedures()).keep(new Filter<StoredProcedureMetadata>() {
            @Override
            public boolean accepts(StoredProcedureMetadata item) {
                return item.getName().equals(procedureName);
            }
        }).first();
        if (procedureMetadata == null) {
            throw new NoSuchProcedureError(entityType, procedureName);
        }
        if (procedureMetadata.getParameters().size() != parameters.length) {
            throw new MismatchedParametersNumberError(entityType, procedureName, procedureMetadata.getParameters().size(), parameters.length);
        }
        for (int i = 0; i < procedureMetadata.getParameters().size(); i++) {
            ParameterMetadata metadata = procedureMetadata.getParameters().get(i);
            if (metadata.getParameterMode().equals(ParameterMode.IN)) {
                if (parameters[i] != null && !ReflectionUtils.mapType(metadata.getParameterType()).isInstance(parameters[i])) {
                    throw new MismatchedParameterTypeError(entityType, procedureName, i, metadata.getParameterType(), parameters[i].getClass());
                }
            } else {
                if (parameters[i] == null) {
                    throw new NullPointerException(metadata.getParameterMode() + " parameter cannot be null");
                }
                if (!(parameters[i] instanceof Reference<?>)) {
                    throw new ReferenceParameterExpectedError(entityType, procedureName, i);
                }
            }
        }
        final ProcedureCallStatement statement = (ProcedureCallStatement) getStatement(entityType, "call." + procedureName, null, StatementType.CALL);
        final Map<String, Object> values = new HashMap<String, Object>();
        for (int i = 0; i < parameters.length; i++) {
            values.put("value.parameter" + i, parameters[i] instanceof Reference ? ((Reference<?>) parameters[i]).getValue() : parameters[i]);
        }
        final CallableStatement callableStatement;
        final ArrayList<Object> result = new ArrayList<Object>();
        try {
            callableStatement = openStatement(statement.prepare(openConnection(), null, values));
            for (int i = 0; i < procedureMetadata.getParameters().size(); i++) {
                final ParameterMetadata metadata = procedureMetadata.getParameters().get(i);
                if (!metadata.getParameterMode().equals(ParameterMode.IN)) {
                    callableStatement.registerOutParameter(i + 1, metadata.getType());
                }
            }
            if (procedureMetadata.getResultType().equals(void.class)) {
                callableStatement.executeUpdate();
            } else {
                final ResultSet resultSet = callableStatement.executeQuery();
                final EntityHandler<Object> entityHandler;
                if (procedureMetadata.isPartial()) {
                    entityHandler = null;
                } else {
                    //noinspection unchecked
                    entityHandler = (EntityHandler<Object>) entityHandlerContext.getHandler(procedureMetadata.getResultType());
                }
                while (resultSet.next()) {
                    final Map<String, Object> map = rowHandler.handleRow(resultSet);
                    if (procedureMetadata.isPartial()) {
                        try {
                            result.add(entityHandlerContext.fromMap(beanInitializer.initialize(procedureMetadata.getResultType(), new Class[0]), getPartialEntityMetadata(procedureMetadata.getResultType()), map));
                        } catch (BeanInstantiationException e) {
                            throw new EntityInitializationError(procedureMetadata.getResultType(), e);
                        }
                    } else {
                        assert entityHandler != null;
                        Object instance = entityContext.getInstance(entityHandler.getEntityType());
                        instance = entityHandler.fromMap(instance, map);
                        if (entityHandler.hasKey() && entityHandler.getKey(instance) != null) {
                            final Serializable key = entityHandler.getKey(instance);
                            if (initializationContext.contains(entityHandler.getEntityType(), key)) {
                                instance = initializationContext.get(entityHandler.getEntityType(), key);
                                result.add(instance);
                                continue;
                            }
                        }
                        prepareEntity(instance, map);
                        result.add(instance);
                    }
                }
                resultSet.close();
            }
            for (int i = 0; i < procedureMetadata.getParameters().size(); i++) {
                final ParameterMetadata metadata = procedureMetadata.getParameters().get(i);
                if (!metadata.getParameterMode().equals(ParameterMode.IN)) {
                    //noinspection unchecked
                    ((Reference) parameters[i]).setValue(callableStatement.getObject(i + 1));
                }
            }
            cleanUpStatement(callableStatement);
        } catch (SQLException e) {
            throw new ProcedureExecutionFailureError("Failed to call procedure " + procedureName, e);
        }
        return result;
    }

    @Override
    public <E> long countAll(Class<E> entityType) {
        return internalCount(entityType, Statements.Manipulation.COUNT_ALL, Collections.<String, Object>emptyMap());
    }

    @Override
    public <E> long count(E sample) {
        final E enhancedEntity = getEnhancedEntity(sample);
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(sample);
        return internalCount(entityHandler.getEntityType(), entityHandler.hasKey() && entityHandler.getKey(enhancedEntity) != null ? Statements.Manipulation.COUNT_ONE : Statements.Manipulation.COUNT_LIKE, MapTools.prefixKeys(entityHandler.toMap(enhancedEntity), "value."));
    }

    @Override
    public <E> boolean exists(E sample) {
        return count(sample) != 0;
    }

    @Override
    public <E, K extends Serializable> boolean exists(Class<E> entityType, K key) {
        final E instance = entityContext.getInstance(entityType);
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entityType);
        entityHandler.setKey(instance, key);
        return count(instance) != 0;
    }

    private <E> long count(Class<E> entityType, Map<String, Object> values) {
        return internalCount(entityType, Statements.Manipulation.COUNT_LIKE, values);
    }

    private <E> boolean exists(Class<E> entityType, Map<String, Object> values) {
        return count(entityType, values) != 0;
    }

    @Override
    public List<Integer> run(BatchOperation batchOperation) {
        startBatch();
        log.info("Stacking operations for batch execution");
        batchOperation.execute(this);
        return endBatch();
    }

    @Override
    public <E> SelectQueryInitiator<E> from(E alias) {
        return new DefaultSelectQueryInitiator<E>(session, alias);
    }

    /**
     * Event handler context
     */

    @Override
    public void addHandler(DataAccessEventHandler eventHandler) {
        this.eventHandler.addHandler(eventHandler);
    }

}
