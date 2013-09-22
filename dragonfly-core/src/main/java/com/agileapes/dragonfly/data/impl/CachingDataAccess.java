package com.agileapes.dragonfly.data.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.couteau.reflection.beans.BeanInitializer;
import com.agileapes.couteau.reflection.beans.impl.ConstructorBeanInitializer;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessObject;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.data.PartialDataAccess;
import com.agileapes.dragonfly.entity.*;
import com.agileapes.dragonfly.entity.impl.DefaultEntityInitializationContext;
import com.agileapes.dragonfly.entity.impl.DefaultRowHandler;
import com.agileapes.dragonfly.entity.impl.ThreadLocalEntityInitializationContext;
import com.agileapes.dragonfly.error.*;
import com.agileapes.dragonfly.events.DataAccessEventHandler;
import com.agileapes.dragonfly.events.EventHandlerContext;
import com.agileapes.dragonfly.events.impl.CompositeDataAccessEventHandler;
import com.agileapes.dragonfly.metadata.impl.ColumnMappingMetadataCollector;
import com.agileapes.dragonfly.security.DataSecurityManager;
import com.agileapes.dragonfly.statement.Statement;
import com.agileapes.dragonfly.statement.StatementType;
import com.agileapes.dragonfly.statement.Statements;
import com.agileapes.dragonfly.tools.MapTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/20, 23:29)
 */
public class CachingDataAccess implements PartialDataAccess, EventHandlerContext {

    private static final Log log = LogFactory.getLog(DataAccess.class);
    private static final Map<Statements.Manipulation, String> STATEMENTS = new ConcurrentHashMap<Statements.Manipulation, String>();
    static {
        STATEMENTS.put(Statements.Manipulation.CALL, "call");
        STATEMENTS.put(Statements.Manipulation.DELETE_ALL, "deleteAll");
        STATEMENTS.put(Statements.Manipulation.DELETE_LIKE, "deleteLike");
        STATEMENTS.put(Statements.Manipulation.DELETE_ONE, "deleteByKey");
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

    public CachingDataAccess(DataAccessSession session, DataSecurityManager securityManager, EntityContext entityContext, EntityHandlerContext entityHandlerContext) {
        this(session, securityManager, entityContext, entityHandlerContext, true);
    }

    public CachingDataAccess(DataAccessSession session, DataSecurityManager securityManager, EntityContext entityContext, EntityHandlerContext entityHandlerContext, boolean autoInitialize) {
        this.session = session;
        this.securityManager = securityManager;
        this.entityContext = entityContext;
        this.entityHandlerContext = entityHandlerContext;
        this.beanInitializer = new ConstructorBeanInitializer();
        this.metadataCollector = new ColumnMappingMetadataCollector();
        this.eventHandler = new CompositeDataAccessEventHandler();
        this.initializationContext = new ThreadLocalEntityInitializationContext(this);
        this.rowHandler = new DefaultRowHandler();
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

    private int internalExecuteUpdate(Class<?> entityType, String statement) {
        return internalExecuteUpdate(entityType, statement, Collections.<String, Object>emptyMap());
    }

    private int internalExecuteUpdate(Class<?> entityType, String statement, Map<String, Object> values) {
        return internalExecuteUpdate(getStatement(entityType, statement, StatementType.INSERT, StatementType.DELETE, StatementType.UPDATE), values);
    }

    private int internalExecuteUpdate(Statement statement, Map<String, Object> values) {
        final PreparedStatement preparedStatement = statement.prepare(session.getConnection(), null, values);
        try {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new StatementExecutionFailureError("Failed to execute statement", e);
        }
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

    private <E> List<E> internalExecuteQuery(Class<E> entityType, String statementName) {
        return internalExecuteQuery(entityType, statementName, Collections.<String, Object>emptyMap());
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

    private <E> List<Map<String, Object>> internalExecuteUntypedQuery(Class<E> entityType, String statementName) {
        return internalExecuteUntypedQuery(entityType, statementName, Collections.<String, Object>emptyMap());
    }

    private <E> List<Map<String, Object>> internalExecuteUntypedQuery(Class<E> entityType, Statements.Manipulation statement) {
        return internalExecuteUntypedQuery(entityType, statement, Collections.<String, Object>emptyMap());
    }

    private <E> List<Map<String, Object>> internalExecuteUntypedQuery(Class<E> entityType, Statements.Manipulation statement, Map<String, Object> values) {
        return internalExecuteUntypedQuery(entityType, STATEMENTS.get(statement), values);
    }

    private <E> List<Map<String, Object>> internalExecuteUntypedQuery(Class<E> entityType, String statementName, Map<String, Object> values) {
        final Statement statement = getStatement(entityType, statementName, StatementType.QUERY);
        final PreparedStatement preparedStatement = statement.prepare(session.getConnection(), null, values);
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entityType);
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

    /**
     * Internal methods for taking advantage of the enhancements provided through couteau-enhancer
     */

    private <E> DataAccessObject<E, ? extends Serializable> getDataAccessObject(E entity) {
        //noinspection unchecked
        return (DataAccessObject<E, ? extends Serializable>) getEnhancedEntity(entity);
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
        entityHandler.loadRelations(enhancedEntity, values, initializationContext);
        initializationContext.unlock();
    }

    /**
     * Partial data access support
     */

    @Override
    public <O> List<O> executeQuery(O sample) {
        return null;
    }

    @Override
    public <O> List<O> executeQuery(Class<O> resultType) {
        return null;
    }

    @Override
    public <O> List<O> executeQuery(Class<O> resultType, Map<String, Object> values) {
        return null;
    }

    @Override
    public <E> List<Map<String, Object>> executeUntypedQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        return null;
    }

    @Override
    public <O> int executeUpdate(O sample) {
        return 0;
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
            eventHandler.beforeInsert(enhancedEntity);
            final PreparedStatement preparedStatement = getStatement(entityHandler.getEntityType(), Statements.Manipulation.INSERT).prepare(session.getConnection(), null, MapTools.prefixKeys(entityHandler.toMap(enhancedEntity), "value."));
            try {
                if (preparedStatement.executeUpdate() <= 0) {
                    throw new UnsuccessfulOperationError("Failed to insert object");
                }
            } catch (SQLException e) {
                throw new UnsupportedOperationException("Failed to insert object", e);
            }
            if (entityHandler.isKeyAutoGenerated()) {
                try {
                    final Serializable key = session.getDatabaseDialect().retrieveKey(preparedStatement.getGeneratedKeys(), session.getMetadataRegistry().getTableMetadata(entityHandler.getEntityType()));
                    entityHandler.setKey(enhancedEntity, key);
                } catch (SQLException e) {
                    throw new UnsupportedOperationException("Failed to retrieve auto-generated keys", e);
                }
            }
            initializedEntity.setOriginalCopy(enhancedEntity);
        } else {
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
            if (internalExecuteUpdate(entityHandler.getEntityType(), Statements.Manipulation.UPDATE, values) <= 0) {
                throw new UnsupportedOperationException("Failed to update object");
            }
        }
        if (!shouldUpdate) {
            eventHandler.afterInsert(enhancedEntity);
        } else {
            eventHandler.afterUpdate(enhancedEntity);
        }
        entityHandler.saveDependentRelations(enhancedEntity, this);
        eventHandler.afterSave(enhancedEntity);
        saveQueue.remove(entity);
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
        eventHandler.beforeDelete(enhancedEntity);
        final Map<String, Object> map = MapTools.prefixKeys(entityHandler.toMap(enhancedEntity), "value.");
        if (exists(entityHandler.getEntityType(), map)) {
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
    public <E> void deleteAll(Class<E> entityType) {
        eventHandler.beforeDeleteAll(entityType);
        internalExecuteUpdate(entityType, Statements.Manipulation.DELETE_ALL);
        eventHandler.afterDeleteAll(entityType);
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
    public <E> List<?> call(Class<E> entityType, String procedureName, Object... parameters) {
        return null;
    }

    @Override
    public <E> long count(Class<E> entityType) {
        return internalCount(entityType, Statements.Manipulation.COUNT_ALL, Collections.<String, Object>emptyMap());
    }

    @Override
    public <E> long count(E sample) {
        final E enhancedEntity = getEnhancedEntity(sample);
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(sample);
        return internalCount(entityHandler.getEntityType(), entityHandler.hasKey() && entityHandler.getKey(enhancedEntity) != null ? Statements.Manipulation.COUNT_ONE : Statements.Manipulation.COUNT_LIKE, entityHandler.toMap(enhancedEntity));
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

    @Override
    public <E> long count(Class<E> entityType, Map<String, Object> values) {
        return internalCount(entityType, Statements.Manipulation.COUNT_LIKE, values);
    }

    @Override
    public <E> boolean exists(Class<E> entityType, Map<String, Object> values) {
        return count(entityType, values) != 0;
    }

    /**
     * Event handler context
     */

    @Override
    public void addHandler(DataAccessEventHandler eventHandler) {
        this.eventHandler.addHandler(eventHandler);
    }

}
