package com.agileapes.dragonfly.data.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.couteau.reflection.beans.BeanInitializer;
import com.agileapes.couteau.reflection.beans.impl.ConstructorBeanInitializer;
import com.agileapes.couteau.reflection.error.BeanInstantiationException;
import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.dragonfly.annotations.ParameterMode;
import com.agileapes.dragonfly.annotations.Partial;
import com.agileapes.dragonfly.data.BatchOperation;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.data.PartialDataAccess;
import com.agileapes.dragonfly.entity.*;
import com.agileapes.dragonfly.entity.impl.*;
import com.agileapes.dragonfly.error.*;
import com.agileapes.dragonfly.events.DataAccessEventHandler;
import com.agileapes.dragonfly.events.EventHandlerContext;
import com.agileapes.dragonfly.events.impl.CompositeDataAccessEventHandler;
import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.metadata.impl.ColumnMappingMetadataCollector;
import com.agileapes.dragonfly.security.DataSecurityManager;
import com.agileapes.dragonfly.security.impl.StoredProcedureSubject;
import com.agileapes.dragonfly.statement.Statement;
import com.agileapes.dragonfly.statement.StatementBuilder;
import com.agileapes.dragonfly.statement.StatementType;
import com.agileapes.dragonfly.statement.Statements;
import com.agileapes.dragonfly.statement.impl.FreemarkerSecondPassStatementBuilder;
import com.agileapes.dragonfly.statement.impl.ProcedureCallStatement;
import com.agileapes.dragonfly.tools.MapTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/20, 23:29)
 */
public class DefaultDataAccess implements PartialDataAccess, EventHandlerContext {

    private static final Log log = LogFactory.getLog(DataAccess.class);
    private static final Map<Statements.Manipulation, String> STATEMENTS = new ConcurrentHashMap<Statements.Manipulation, String>();

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
    private final DataSecurityManager securityManager;
    private final EntityContext entityContext;
    private final EntityHandlerContext entityHandlerContext;
    private final BeanInitializer beanInitializer;
    private final ColumnMappingMetadataCollector metadataCollector;
    private final CompositeDataAccessEventHandler eventHandler;
    private final EntityInitializationContext initializationContext;
    private final RowHandler rowHandler;
    private final ThreadLocal<Map<Object, Object>> saveQueue;
    private final ThreadLocal<Set<Object>> deleteQueue;
    private final EntityMapCreator mapCreator;
    private final MapEntityCreator entityCreator;
    private final Map<Class<?>, Collection<ColumnMetadata>> partialEntityColumns = new ConcurrentHashMap<Class<?>, Collection<ColumnMetadata>>();
    private final ThreadLocal<Map<Class<?>, Map<Statements.Manipulation, Set<Statement>>>> deleteAllStatements;
    private final ThreadLocal<Boolean> batchMode;
    private final ThreadLocal<PreparedStatement> batchStatement;
    private final StatementPreparator statementPreparator;
    private final ThreadLocal<List<Object>> deferredKeys;

    public DefaultDataAccess(DataAccessSession session, DataSecurityManager securityManager, EntityContext entityContext, EntityHandlerContext entityHandlerContext) {
        this(session, securityManager, entityContext, entityHandlerContext, true);
    }

    public DefaultDataAccess(DataAccessSession session, DataSecurityManager securityManager, EntityContext entityContext, EntityHandlerContext entityHandlerContext, boolean autoInitialize) {
        this.session = session;
        this.securityManager = securityManager;
        this.entityContext = entityContext;
        this.entityHandlerContext = entityHandlerContext;
        this.beanInitializer = new ConstructorBeanInitializer();
        this.metadataCollector = new ColumnMappingMetadataCollector();
        this.eventHandler = new CompositeDataAccessEventHandler();
        this.initializationContext = new ThreadLocalEntityInitializationContext(this);
        this.rowHandler = new DefaultRowHandler();
        this.mapCreator = new DefaultEntityMapCreator();
        this.entityCreator = new DefaultMapEntityCreator();
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
        if (entityContext instanceof DefaultEntityContext) {
            ((DefaultEntityContext) entityContext).setDataAccess(this);
        }
        this.batchMode = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return false;
            }
        };
        this.batchStatement = new ThreadLocal<PreparedStatement>();
        this.statementPreparator = new DefaultStatementPreparator(false);
        this.deferredKeys = new ThreadLocal<List<Object>>() {
            @Override
            protected List<Object> initialValue() {
                return new ArrayList<Object>();
            }
        };
        if (autoInitialize) {
            log.info("Automatically initializing the session");
            synchronized (this.session) {
                if (!this.session.isInitialized()) {
                    this.session.initialize();
                }
            }
        }
    }

    /**
     * Internal update methods
     * These are helpers for the rest of the interface
     */

    private int internalExecuteUpdate(Class<?> entityType, Statements.Manipulation statementName) {
        return internalExecuteUpdate(entityType, statementName, Collections.<String, Object>emptyMap());
    }

    private int internalExecuteUpdate(Class<?> entityType, Statements.Manipulation statement, Map<String, Object> values) {
        return internalExecuteUpdate(getStatement(entityType, statement, StatementType.INSERT, StatementType.DELETE, StatementType.UPDATE), values);
    }

    private int internalExecuteUpdate(Class<?> entityType, String statement, Map<String, Object> values) {
        return internalExecuteUpdate(getStatement(entityType, statement, StatementType.INSERT, StatementType.DELETE, StatementType.UPDATE), values);
    }

    private int internalExecuteUpdate(Statement statement, Map<String, Object> values) {
        if (isInBatchMode()) {
            final PreparedStatement preparedStatement;
            if (batchStatement.get() == null) {
                final Connection connection = session.getConnection();
                try {
                    connection.setAutoCommit(false);
                    if (!connection.getMetaData().supportsBatchUpdates()) {
                        throw new UnsupportedOperationException("Batch updates are not supported by your database");
                    }
                } catch (SQLException e) {
                    throw new BatchOperationInterruptedError("Failed to disable auto-commit", e);
                }
                preparedStatement = statement.prepare(connection, null, values);
                batchStatement.set(preparedStatement);
            } else {
                preparedStatement = batchStatement.get();
                String sql = statement.getSql();
                if (statement.isDynamic()) {
                    sql = new FreemarkerSecondPassStatementBuilder(statement, session.getDatabaseDialect(), values).getStatement(statement.getTableMetadata()).getSql();
                }
                statementPreparator.prepare(preparedStatement, statement.getTableMetadata(), values, sql);
            }
            try {
                preparedStatement.addBatch();
            } catch (SQLException e) {
                throw new BatchOperationInterruptedError("Failed to add batch statement", e);
            }
            return -1;
        } else {
            final PreparedStatement preparedStatement = statement.prepare(session.getConnection(), null, values);
            try {
                return preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new StatementExecutionFailureError("Failed to execute statement", e);
            }
        }
    }

    private synchronized void startBatch() {
        log.info("Starting batch operation");
        if (isInBatchMode()) {
            throw new BatchOperationInterruptedError("Batch operation already in progress");
        }
        batchMode.set(true);
        batchStatement.remove();
    }

    private synchronized List<Integer> endBatch() {
        log.info("Concluding batch operation");
        if (!isInBatchMode()) {
            throw new BatchOperationInterruptedError("No batch operation has been started");
        }
        batchMode.set(false);
        final PreparedStatement preparedStatement = batchStatement.get();
        if (preparedStatement == null) {
            return Collections.emptyList();
        }
        batchStatement.remove();
        final int[] batchResult;
        try {
            log.info("Executing stacked operations");
            batchResult = preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new BatchOperationInterruptedError("Failed to execute batch operations", e);
        }
        final Connection connection;
        try {
            connection = preparedStatement.getConnection();
        } catch (SQLException e) {
            throw new BatchOperationInterruptedError("Failed to obtain statement connection", e);
        }
        try {
            log.info("Informing the database of the changes");
            connection.commit();
        } catch (SQLException e) {
            throw new BatchOperationInterruptedError("Failed to commit batch results", e);
        }
        try {
            final ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            final List<Object> objects = new ArrayList<Object>(deferredKeys.get());
            deferredKeys.get().clear();
            for (Object object : objects) {
                if (!generatedKeys.next()) {
                    throw new BatchOperationInterruptedError("Failed to retrieve key for entity");
                }
                final Serializable serializable = session.getDatabaseDialect().retrieveKey(generatedKeys);
                entityHandlerContext.getHandler(object).setKey(object, serializable);
            }
        } catch (SQLException e) {
            throw new BatchOperationInterruptedError("Failed to load generated keys", e);
        }
        final ArrayList<Integer> result = new ArrayList<Integer>();
        for (int item : batchResult) {
            result.add(item);
        }
        return result;
    }

    /**
     * Internal query methods
     */

    private <E> List<E> internalExecuteQuery(Class<E> entityType, Statements.Manipulation statement) {
        return internalExecuteQuery(entityType, statement, Collections.<String, Object>emptyMap());
    }

    private <E> List<E> internalExecuteQuery(Class<E> entityType, Statements.Manipulation statement, Map<String, Object> values) {
        return internalExecuteQuery(entityType, STATEMENTS.get(statement), values);
    }

    private <E> List<E> internalExecuteQuery(Class<E> entityType, String statementName, Map<String, Object> values) {
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entityType);
        final List<Map<String, Object>> maps = internalExecuteUntypedQuery(entityType, statementName, values);
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

    private <E> List<Map<String, Object>> internalExecuteUntypedQuery(Class<E> entityType, Statements.Manipulation statement, Map<String, Object> values) {
        return internalExecuteUntypedQuery(entityType, STATEMENTS.get(statement), values);
    }

    private <E> List<Map<String, Object>> internalExecuteUntypedQuery(Class<E> entityType, String statementName, Map<String, Object> values) {
        if (isInBatchMode() && !statementName.startsWith("count")) {
            throw new BatchOperationInterruptedError("Batch operation interrupted by query");
        }
        final Statement statement = getStatement(entityType, statementName, StatementType.QUERY);
        final PreparedStatement preparedStatement = statement.prepare(session.getConnection(), null, values);
        final ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(rowHandler.handleRow(resultSet));
            }
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to retrieve result set from the database", e);
        }
        return result;
    }

    private <E> long internalCount(Class<E> entityType, Statements.Manipulation statement, Map<String, Object> values) {
        final List<Map<String, Object>> list = internalExecuteUntypedQuery(entityType, statement, values);
        if (list.size() != 1) {
            throw new UnsuccessfulOperationError("Failed to execute statement");
        }
        return (Long) list.get(0).get(with(list.get(0).keySet()).find(new Filter<String>() {
            @Override
            public boolean accepts(String item) {
                return session.getDatabaseDialect().getCountColumn().equalsIgnoreCase(item);
            }
        }));
    }

    /**
     * Internal statement access methods
     */

    private Statement getStatement(Class<?> entityType, Statements.Manipulation statement, StatementType... expected) {
        return getStatement(entityType, STATEMENTS.get(statement), expected);
    }

    private Statement getStatement(Class<?> entityType, String statementName, StatementType... expected) {
        Statement result;
        try {
            result = session.getStatementRegistry(entityType).get(statementName);
        } catch (RegistryException e) {
            throw new UnrecognizedQueryError(entityType, statementName);
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
        return result;
    }

    private <E> void prepareEntity(E entity, Map<String, Object> values) {
        final E enhancedEntity = getEnhancedEntity(entity);
        final InitializedEntity<E> initializedEntity = getInitializedEntity(enhancedEntity);
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entity);
        final EntityInitializationContext initializationContext;
        if (initializedEntity.getInitializationContext() != null) {
            initializationContext = initializedEntity.getInitializationContext();
        } else {
            initializationContext = new DefaultEntityInitializationContext(this, this.initializationContext);
            final Serializable key = entityHandler.getKey(enhancedEntity);
            if (entityHandler.hasKey() && key != null) {
                initializationContext.register(entityHandler.getEntityType(), key, enhancedEntity);
            }
            initializedEntity.setInitializationContext(initializationContext);
        }
        initializationContext.lock();
        entityHandler.loadEagerRelations(enhancedEntity, values, initializationContext);
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
        final Statement statement = getStatement(entityType, queryName, StatementType.QUERY);
        final PreparedStatement preparedStatement = statement.prepare(session.getConnection(), null, values);
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
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to load result set", e);
        }
        return result;
    }

    @Override
    public <O> int executeUpdate(O sample) {
        final Class<?> resultType = sample.getClass();
        if (!resultType.isAnnotationPresent(Partial.class)) {
            throw new PartialEntityDefinitionError("Expected to find @Partial on " + resultType.getCanonicalName());
        }
        final Partial annotation = resultType.getAnnotation(Partial.class);
        final Statement statement = getStatement(annotation.targetEntity(), annotation.query(), StatementType.INSERT, StatementType.DELETE, StatementType.UPDATE);
        final PreparedStatement preparedStatement = statement.prepare(session.getConnection(), mapCreator, sample);
        try {
            return preparedStatement.executeUpdate();
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
        saveQueue.put(entity, enhancedEntity);
        final InitializedEntity<E> initializedEntity = getInitializedEntity(enhancedEntity);
        eventHandler.beforeSave(enhancedEntity);
        entityHandler.saveDependencyRelations(enhancedEntity, this);
        boolean shouldUpdate = entityHandler.hasKey() && entityHandler.getKey(entity) != null;
        if (!shouldUpdate) {
            insert(entityHandler, enhancedEntity, initializedEntity);
        } else {
            update(entityHandler, enhancedEntity, initializedEntity);
        }
        entityHandler.saveDependentRelations(enhancedEntity, this);
        eventHandler.afterSave(enhancedEntity);
        saveQueue.remove(entity);
        return enhancedEntity;
    }

    private <E> void update(EntityHandler<E> entityHandler, E enhancedEntity, InitializedEntity<E> initializedEntity) {
        eventHandler.beforeUpdate(enhancedEntity);
        final Map<String, Object> current = entityHandler.toMap(enhancedEntity);
        final Map<String, Object> values = new HashMap<String, Object>();
        values.putAll(MapTools.prefixKeys(current, "value."));
        values.putAll(MapTools.prefixKeys(current, "new."));
        final E originalCopy = initializedEntity.getOriginalCopy();
        final Map<String, Object> original = entityHandler.toMap(originalCopy);
        values.putAll(MapTools.prefixKeys(original, "old."));
        for (String key : original.keySet()) {
            if (!values.containsKey("value." + key)) {
                values.put("value." + key, original.get(key));
            }
        }
        if (internalExecuteUpdate(entityHandler.getEntityType(), Statements.Manipulation.UPDATE, values) <= 0 && !isInBatchMode()) {
            throw new UnsuccessfulOperationError("Failed to update object");
        }
        eventHandler.afterUpdate(enhancedEntity);
    }

    private <E> void insert(EntityHandler<E> entityHandler, E enhancedEntity, InitializedEntity<E> initializedEntity) {
        eventHandler.beforeInsert(enhancedEntity);
        final PreparedStatement preparedStatement;
        final Statement statement = getStatement(entityHandler.getEntityType(), Statements.Manipulation.INSERT);
        final Map<String, Object> objectMap = MapTools.prefixKeys(entityHandler.toMap(enhancedEntity), "value.");
        if (isInBatchMode() && batchStatement.get() != null) {
            preparedStatement = batchStatement.get();
            String sql = statement.getSql();
            if (statement.isDynamic()) {
                sql = new FreemarkerSecondPassStatementBuilder(statement, session.getDatabaseDialect(), objectMap).getStatement(statement.getTableMetadata()).getSql();
            }
            statementPreparator.prepare(preparedStatement, statement.getTableMetadata(), objectMap, sql);
        } else {
            final Connection connection = session.getConnection();
            if (isInBatchMode()) {
                try {
                    connection.setAutoCommit(false);
                    if (!connection.getMetaData().supportsBatchUpdates()) {
                        throw new UnsupportedOperationException("Batch updates are not supported by your database");
                    }
                } catch (SQLException e) {
                    throw new UnsuccessfulOperationError("Failed to disable auto-commit on connection", e);
                }
            }
            preparedStatement = statement.prepare(connection, null, objectMap);
            if (isInBatchMode()) {
                batchStatement.set(preparedStatement);
            }
        }
        if (isInBatchMode()) {
            try {
                preparedStatement.addBatch();
            } catch (SQLException e) {
                throw new BatchOperationInterruptedError("Failed to add batch operation", e);
            }
        } else {
            try {
                if (preparedStatement.executeUpdate() <= 0) {
                    throw new UnsuccessfulOperationError("Failed to insert object");
                }
            } catch (SQLException e) {
                throw new UnsupportedOperationException("Failed to insert object", e);
            }
        }
        if (entityHandler.isKeyAutoGenerated()) {
            if (isInBatchMode()) {
                deferredKeys.get().add(enhancedEntity);
            } else {
                try {
                    entityHandler.setKey(enhancedEntity, session.getDatabaseDialect().retrieveKey(preparedStatement.getGeneratedKeys()));
                } catch (SQLException e) {
                    throw new UnsupportedOperationException("Failed to retrieve auto-generated keys", e);
                }
            }
        }
        initializedEntity.setOriginalCopy(enhancedEntity);
        eventHandler.afterInsert(enhancedEntity);
    }

    private Boolean isInBatchMode() {
        return batchMode.get();
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
        eventHandler.beforeDelete(enhancedEntity);
        final Map<String, Object> map = MapTools.prefixKeys(entityHandler.toMap(enhancedEntity), "value.");
        if ((entityHandler.hasKey() && entityHandler.getKey(enhancedEntity) != null && exists(entityHandler.getEntityType(), entityHandler.getKey(enhancedEntity))) || exists(entityHandler.getEntityType(), map)) {
            entityHandler.deleteDependencyRelations(enhancedEntity, this);
            final Statements.Manipulation statement;
            if (entityHandler.hasKey() && entityHandler.getKey(enhancedEntity) != null) {
                statement = Statements.Manipulation.DELETE_ONE;
            } else {
                statement = Statements.Manipulation.DELETE_LIKE;
            }
            internalExecuteUpdate(entityHandler.getEntityType(), statement, map);
            entityHandler.deleteDependentRelations(enhancedEntity, this);
        }
        eventHandler.afterDelete(enhancedEntity);
        deleteQueue.remove(entity);
    }

    @Override
    public <E, K extends Serializable> void delete(Class<E> entityType, K key) {
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entityType);
        final E instance = entityContext.getInstance(entityType);
        entityHandler.setKey(instance, key);
        eventHandler.beforeDelete(entityType, key);
        delete(instance);
        eventHandler.afterDelete(entityType, key);
    }

    @Override
    public <E> void deleteAll(final Class<E> entityType) {
        eventHandler.beforeDeleteAll(entityType);
        deleteDependents(entityType);
        internalExecuteUpdate(entityType, Statements.Manipulation.DELETE_ALL);
        deleteDependencies(entityType);
        eventHandler.afterDeleteAll(entityType);
    }

    private synchronized <E> void deleteDependents(Class<E> entityType) {
        prepareDeleteAllStatements(entityType, Statements.Manipulation.DELETE_DEPENDENTS);
        with(deleteAllStatements.get().get(entityType).get(Statements.Manipulation.DELETE_DEPENDENTS)).each(new Processor<Statement>() {
            @Override
            public void process(Statement statement) {
                internalExecuteUpdate(statement, Collections.<String, Object>emptyMap());
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
            final TableMetadata<E> tableMetadata = session.getMetadataRegistry().getTableMetadata(entityType);
            with(tableMetadata.getForeignReferences())
                    .forThose(new Filter<ReferenceMetadata<E, ?>>() {
                                  @Override
                                  public boolean accepts(ReferenceMetadata<E, ?> referenceMetadata) {
                                      return referenceMetadata.getCascadeMetadata().cascadeRemove() && (statementType.equals(Statements.Manipulation.DELETE_DEPENDENCIES) ? referenceMetadata.isRelationOwner() : !referenceMetadata.isRelationOwner());
                                  }
                              },
                            new Processor<ReferenceMetadata<E, ?>>() {
                                @Override
                                public void process(ReferenceMetadata<E, ?> referenceMetadata) {
                                    final StatementBuilder builder = session.getDatabaseDialect().getStatementBuilderContext().getManipulationStatementBuilder(statementType);
                                    statements.add(builder.getStatement(tableMetadata, referenceMetadata));
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
        eventHandler.afterTruncate(entityType);
    }

    @Override
    public <E> List<E> find(E sample) {
        final E enhancedEntity = getEnhancedEntity(sample);
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(sample);
        eventHandler.beforeFind(enhancedEntity);
        final List<E> found = internalExecuteQuery(entityHandler.getEntityType(), Statements.Manipulation.FIND_LIKE, MapTools.prefixKeys(entityHandler.toMap(enhancedEntity), "value."));
        eventHandler.afterFind(enhancedEntity, found);
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
        final List<E> list = internalExecuteQuery(entityType, Statements.Manipulation.FIND_ONE, map);
        final E result;
        if (list.isEmpty()) {
            result = null;
        } else if (list.size() == 1) {
            result = list.get(0);
        } else {
            throw new EntityDefinitionError("More than one item correspond to type " + entityType.getCanonicalName() + " and key " + key);
        }
        eventHandler.afterFind(entityType, key, result);
        return result;
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType) {
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entityType);
        eventHandler.beforeFindAll(entityType);
        final List<E> found = internalExecuteQuery(entityHandler.getEntityType(), Statements.Manipulation.FIND_ALL);
        eventHandler.afterFindAll(entityType, found);
        return found;
    }

    @Override
    public <E> int executeUpdate(Class<E> entityType, String queryName, Map<String, Object> values) {
        eventHandler.beforeExecuteUpdate(entityType, queryName, values);
        final int affectedItems = internalExecuteUpdate(entityType, queryName, values);
        eventHandler.afterExecuteUpdate(entityType, queryName, values, affectedItems);
        return affectedItems;
    }

    @Override
    public <E> int executeUpdate(E sample, String queryName) {
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(sample);
        final E enhancedEntity = getEnhancedEntity(sample);
        eventHandler.beforeExecuteUpdate(enhancedEntity, queryName);
        final int affectedItems = internalExecuteUpdate(entityHandler.getEntityType(), queryName, entityHandler.toMap(enhancedEntity));
        eventHandler.afterExecuteUpdate(enhancedEntity, queryName, affectedItems);
        return affectedItems;
    }

    @Override
    public <E> List<E> executeQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        eventHandler.beforeExecuteQuery(entityType, queryName, values);
        final List<E> list = internalExecuteQuery(entityType, queryName, values);
        eventHandler.afterExecuteQuery(entityType, queryName, values, list);
        return list;
    }

    @Override
    public <E> List<E> executeQuery(E sample, String queryName) {
        final E enhancedEntity = getEnhancedEntity(sample);
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(enhancedEntity);
        eventHandler.beforeExecuteQuery(enhancedEntity, queryName);
        final List<E> list = internalExecuteQuery(entityHandler.getEntityType(), queryName, MapTools.prefixKeys(entityHandler.toMap(enhancedEntity), "value."));
        eventHandler.afterExecuteQuery(enhancedEntity, queryName, list);
        return list;
    }

    @Override
    public <E> List<?> call(Class<E> entityType, final String procedureName, Object... parameters) {
        if (isInBatchMode()) {
            throw new BatchOperationInterruptedError("Batch operation interrupted by procedure call");
        }
        log.info("Calling to stored procedure " + entityType.getCanonicalName() + "." + procedureName);
        final TableMetadata<E> tableMetadata = session.getMetadataRegistry().getTableMetadata(entityType);
        //noinspection unchecked
        final StoredProcedureMetadata procedureMetadata = with(tableMetadata.getProcedures()).keep(new Filter<StoredProcedureMetadata>() {
            @Override
            public boolean accepts(StoredProcedureMetadata item) {
                return item.getName().equals(procedureName);
            }
        }).first();
        if (procedureMetadata == null) {
            throw new UnrecognizedProcedureError(entityType, procedureName);
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
        securityManager.checkAccess(new StoredProcedureSubject(procedureMetadata, parameters));
        final ProcedureCallStatement statement = (ProcedureCallStatement) getStatement(entityType, "call." + procedureName, StatementType.CALL);
        final Map<String, Object> values = new HashMap<String, Object>();
        for (int i = 0; i < parameters.length; i++) {
            values.put("value.parameter" + i, parameters[i] instanceof Reference ? ((Reference<?>) parameters[i]).getValue() : parameters[i]);
        }
        final CallableStatement callableStatement;
        final ArrayList<Object> result = new ArrayList<Object>();
        try {
            callableStatement = statement.prepare(session.getConnection(), null, values);
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
            }
            for (int i = 0; i < procedureMetadata.getParameters().size(); i++) {
                final ParameterMetadata metadata = procedureMetadata.getParameters().get(i);
                if (!metadata.getParameterMode().equals(ParameterMode.IN)) {
                    //noinspection unchecked
                    ((Reference) parameters[i]).setValue(callableStatement.getObject(i + 1));
                }
            }
        } catch (SQLException e) {
            throw new StatementExecutionFailureError("Failed to call procedure " + procedureName, e);
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
    public List<Integer> update(BatchOperation batchOperation) {
        startBatch();
        log.info("Stacking operations for batch execution");
        batchOperation.execute(this);
        return endBatch();
    }

    /**
     * Event handler context
     */

    @Override
    public void addHandler(DataAccessEventHandler eventHandler) {
        this.eventHandler.addHandler(eventHandler);
    }

}
