package com.agileapes.dragonfly.data.impl;

import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.couteau.reflection.beans.BeanInitializer;
import com.agileapes.couteau.reflection.beans.impl.ConstructorBeanInitializer;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessObject;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.data.PartialDataAccess;
import com.agileapes.dragonfly.entity.*;
import com.agileapes.dragonfly.entity.impl.ThreadLocalEntityInitializationContext;
import com.agileapes.dragonfly.error.InvalidStatementTypeError;
import com.agileapes.dragonfly.error.StatementExecutionFailureError;
import com.agileapes.dragonfly.error.UnrecognizedQueryError;
import com.agileapes.dragonfly.error.UnsuccessfulOperationError;
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
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public <E> void save(E entity) {
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entity);
        final E enhancedEntity = getEnhancedEntity(entity);
        final InitializedEntity<E> initializedEntity = getInitializedEntity(enhancedEntity);
        eventHandler.beforeSave(enhancedEntity);
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
                    entityHandler.setKey(entity, key);
                } catch (SQLException e) {
                    throw new UnsupportedOperationException("Failed to retrieve auto-generated keys", e);
                }
            }
            initializedEntity.setOriginalCopy(enhancedEntity);
            eventHandler.afterInsert(enhancedEntity);
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
            eventHandler.afterUpdate(enhancedEntity);
        }
        eventHandler.afterSave(enhancedEntity);
    }

    @Override
    public <E> void delete(E entity) {
        final EntityHandler<E> entityHandler = entityHandlerContext.getHandler(entity);
        final E enhancedEntity = getEnhancedEntity(entity);
        eventHandler.beforeDelete(enhancedEntity);
        final Map<String, Object> map = entityHandler.toMap(enhancedEntity);
        //todo delete cascading relations
        final Statements.Manipulation statement;
        if (entityHandler.hasKey() && entityHandler.getKey(enhancedEntity) != null) {
            statement = Statements.Manipulation.DELETE_ONE;
        } else {
            statement = Statements.Manipulation.DELETE_ALL;
        }
        internalExecuteUpdate(entityHandler.getEntityType(), statement, map);
        eventHandler.afterDelete(enhancedEntity);
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
        return null;
    }

    @Override
    public <E, K extends Serializable> E find(Class<E> entityType, K key) {
        return null;
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType) {
        return null;
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
        return null;
    }

    @Override
    public <E> List<E> executeQuery(E sample, String queryName) {
        return null;
    }

    @Override
    public <E> List<?> call(Class<E> entityType, String procedureName, Object... parameters) {
        return null;
    }

    /**
     * Event handler context
     */

    @Override
    public void addHandler(DataAccessEventHandler eventHandler) {
        this.eventHandler.addHandler(eventHandler);
    }

}
