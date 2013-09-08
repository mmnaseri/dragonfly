package com.agileapes.dragonfly.api.impl;

import com.agileapes.couteau.basics.assets.Assert;
import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.couteau.reflection.beans.BeanInitializer;
import com.agileapes.couteau.reflection.beans.impl.ConstructorBeanInitializer;
import com.agileapes.couteau.reflection.error.BeanInstantiationException;
import com.agileapes.dragonfly.api.DataAccessObject;
import com.agileapes.dragonfly.api.PartialDataAccess;
import com.agileapes.dragonfly.api.annotations.Partial;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.entity.*;
import com.agileapes.dragonfly.entity.impl.DefaultEntityContext;
import com.agileapes.dragonfly.entity.impl.DefaultEntityMapCreator;
import com.agileapes.dragonfly.entity.impl.DefaultEntityRowHandler;
import com.agileapes.dragonfly.entity.impl.DefaultMapEntityCreator;
import com.agileapes.dragonfly.error.*;
import com.agileapes.dragonfly.events.DataAccessEventHandler;
import com.agileapes.dragonfly.events.EventHandlerContext;
import com.agileapes.dragonfly.events.impl.CompositeDataAccessEventHandler;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.impl.ColumnMappingMetadataCollector;
import com.agileapes.dragonfly.statement.Statement;
import com.agileapes.dragonfly.statement.StatementType;
import com.agileapes.dragonfly.tools.MapTools;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>This is the default implementation of the {@link com.agileapes.dragonfly.api.DataAccess}
 * and {@link PartialDataAccess} interfaces, using the available session and provided metadata
 * registries for all actions.</p>
 *
 * <p>This implementation relies on reflection for providing conversion of entities to maps
 * and vice versa.</p>
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 19:16)
 */
public class DefaultDataAccess implements PartialDataAccess, ModifiableEntityContext, EventHandlerContext {

    private final DataAccessSession session;
    private final ModifiableEntityContext entityContext;
    private final EntityRowHandler rowHandler;
    private final MapEntityCreator entityCreator;
    private final EntityMapCreator mapCreator;
    private final BeanInitializer beanInitializer;
    private final ColumnMappingMetadataCollector columnMappingMetadataCollector;
    private final CompositeDataAccessEventHandler eventHandler;

    public DefaultDataAccess(DataAccessSession session) {
        this(session, true);
    }

    public DefaultDataAccess(DataAccessSession session, boolean autoInitialize) {
        this.session = session;
        if (autoInitialize) {
            synchronized (this.session) {
                if (!this.session.isInitialized()) {
                    this.session.initialize();
                }
            }
        }
        this.entityContext = new DefaultEntityContext(this);
        this.rowHandler = new DefaultEntityRowHandler();
        this.entityCreator = new DefaultMapEntityCreator(entityContext);
        this.mapCreator = new DefaultEntityMapCreator();
        beanInitializer = new ConstructorBeanInitializer();
        columnMappingMetadataCollector = new ColumnMappingMetadataCollector();
        eventHandler = new CompositeDataAccessEventHandler();
    }

    @Override
    public <E> void save(E entity) {
        eventHandler.beforeSave(entity);
        final DataAccessObject<E, Serializable> object = checkEntity(entity);
        //noinspection unchecked
        int affectedRows = 0;
        final boolean shouldUpdate;
        try {
            shouldUpdate = (object.hasKey() && object.accessKey() != null) || ((InitializedEntity) object).isDirtied() && find(entity).size() == 1;
            if (shouldUpdate) {
                eventHandler.beforeUpdate(entity);
                //noinspection unchecked
                affectedRows = internalExecuteUpdate(((InitializedEntity<E>) object).getOriginalCopy(), entity, "updateBySample");
            } else {
                eventHandler.beforeInsert(entity);
                affectedRows = internalExecuteUpdate(entity, "insert", true);
            }
        } catch (Exception e) {
            throw new StatementExecutionFailureError("Failed to execute save statement", e);
        }
        if (affectedRows <= 0) {
            throw new UnsuccessfulOperationError("Failed to save entity " + object.getQualifiedName());
        }
        if (shouldUpdate) {
            eventHandler.afterUpdate(entity);
        } else {
            eventHandler.afterInsert(entity);
        }
        eventHandler.afterSave(entity);
    }

    @Override
    public <E> void delete(E entity) {
        eventHandler.beforeDelete(entity);
        if (internalExecuteUpdate(entity, "deleteLike", true) <= 0) {
            throw new UnsupportedOperationException("Failed to delete entity");
        }
        eventHandler.afterDelete(entity);
    }

    @Override
    public <E, K extends Serializable> void delete(Class<E> entityType, K key) {
        eventHandler.beforeDelete(entityType, key);
        final E entity = getInstance(entityType);
        //noinspection unchecked
        final DataAccessObject<E, K> object = (DataAccessObject<E, K>) entity;
        object.changeKey(key);
        delete(entity);
        eventHandler.afterDelete(entityType, key);
    }

    @Override
    public <E> void deleteAll(Class<E> entityType) {
        eventHandler.beforeDeleteAll(entityType);
        internalExecuteUpdate(getInstance(entityType), "deleteAll", true);
        eventHandler.afterDeleteAll(entityType);
    }

    @Override
    public <E> void truncate(Class<E> entityType) {
        eventHandler.beforeTruncate(entityType);
        executeUpdate(entityType, "truncate", Collections.<String, Object>emptyMap());
        eventHandler.afterTruncate(entityType);
    }

    @Override
    public <E> List<E> find(E sample) {
        eventHandler.beforeFind(sample);
        final List<E> list = internalExecuteQuery(sample, "findLike");
        eventHandler.afterFind(sample, list);
        return list;
    }

    @Override
    public <E, K extends Serializable> E find(Class<E> entityType, K key) {
        eventHandler.beforeFind(entityType, key);
        final E entity = getInstance(entityType);
        //noinspection unchecked
        final DataAccessObject<E, K> object = (DataAccessObject<E, K>) entity;
        object.changeKey(key);
        final List<E> result = internalExecuteQuery(entity, "findByKey");
        if (result.isEmpty()) {
            throw new ObjectNotFoundError(entityType, key);
        }
        if (result.size() > 1) {
            throw new AmbiguousObjectKeyError(entityType, key);
        }
        final E found = result.get(0);
        eventHandler.afterFind(entityType, key, found);
        return found;
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType) {
        eventHandler.beforeFindAll(entityType);
        final List<E> list = executeQuery(entityType, "findAll", Collections.<String, Object>emptyMap());
        eventHandler.afterFindAll(entityType, list);
        return list;
    }

    @Override
    public <E, K extends Serializable> K getKey(E entity) {
        //noinspection unchecked
        return (K) checkEntity(entity).accessKey();
    }

    private <E> int internalExecuteUpdate(E original, E replacement, String queryName) {
        final DataAccessObject<E, Serializable> object = checkEntity(original);
        final Map<String, Object> map = MapTools.prefixKeys(mapCreator.toMap(object.getTableMetadata(), original), "value.");
        map.putAll(MapTools.prefixKeys(mapCreator.toMap(object.getTableMetadata(), original), "old."));
        map.putAll(MapTools.prefixKeys(mapCreator.toMap(object.getTableMetadata(), replacement), "new."));
        return executeUpdate(object.getTableMetadata().getEntityType(), queryName, map);
    }

    private <E> List<E> internalExecuteQuery(E sample, String queryName) {
        final DataAccessObject<E, Serializable> object = checkEntity(sample);
        return executeQuery(object.getTableMetadata().getEntityType(), queryName, MapTools.prefixKeys(mapCreator.toMap(object.getTableMetadata(), sample), "value."));
    }

    @Override
    public <E> int executeUpdate(Class<E> entityType, String queryName, Map<String, Object> values) {
        eventHandler.beforeExecuteUpdate(entityType, queryName, values);
        final int affectedRows;
        try {
            final Statement statement = session.getStatementRegistry().get(entityType.getCanonicalName() + "." + queryName);
            if (StatementType.DEFINITION.equals(statement.getType()) || StatementType.QUERY.equals(statement.getType())) {
                throw new InvalidStatementTypeError(statement.getType());
            }
            affectedRows = statement.prepare(session.getConnection(), values).executeUpdate();
        } catch (RegistryException e) {
            throw new UnrecognizedStatementError(entityType, queryName);
        } catch (SQLException e) {
            throw new StatementExecutionFailureError("Failed to execute update statement " + queryName, e);
        }
        return affectedRows;
    }

    @Override
    public <E> int executeUpdate(E sample, String queryName) {
        eventHandler.beforeExecuteUpdate(sample, queryName);
        final int affectedRows = internalExecuteUpdate(sample, queryName, false);
        eventHandler.afterExecuteUpdate(sample, queryName, affectedRows);
        return affectedRows;
    }

    private <E> int internalExecuteUpdate(E sample, String queryName, boolean prefix) {
        final DataAccessObject<E, Serializable> object = checkEntity(sample);
        final Map<String, Object> map = new HashMap<String, Object>();
        if (prefix) {
            map.putAll(MapTools.prefixKeys(mapCreator.toMap(object.getTableMetadata(), sample), "value."));
        } else {
            map.putAll(mapCreator.toMap(object.getTableMetadata(), sample));
        }
        final int affectedRows;
        try {
            final Statement statement = session.getStatementRegistry().get(object.getQualifiedName() + "." + queryName);
            final PreparedStatement preparedStatement = statement.prepare(session.getConnection(), map);
            affectedRows = preparedStatement.executeUpdate();
            if (StatementType.INSERT.equals(statement.getType()) && object.hasKey() && object.isKeyAutoGenerated()) {
                if (affectedRows <= 0) {
                    throw new UnsuccessfulOperationError("Failed to insert item");
                }
                final ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                final Serializable key = session.getDatabaseDialect().retrieveKey(generatedKeys, object.getTableMetadata());
                if (key == null) {
                    throw new UnsuccessfulOperationError("Failed to obtain generated key values for the entity");
                }
                object.changeKey(key);
            }
        } catch (SQLException e) {
            throw new StatementExecutionFailureError("Failed to execute update", e);
        } catch (RegistryException e) {
            throw new UnrecognizedStatementError(object.getTableMetadata().getEntityType(), queryName);
        }
        return affectedRows;
    }

    @Override
    public <E> List<E> executeQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        eventHandler.beforeExecuteQuery(entityType, queryName, values);
        final ArrayList<E> result;
        try {
            final Statement statement = session.getStatementRegistry().get(entityType.getCanonicalName() + "." + queryName);
            if (!StatementType.QUERY.equals(statement.getType())) {
                throw new InvalidStatementTypeError(statement.getType());
            }
            final PreparedStatement preparedStatement = statement.prepare(session.getConnection(), values);
            final ResultSet resultSet = preparedStatement.executeQuery();
            result = new ArrayList<E>();
            while (resultSet.next()) {
                final E entity = getInstance(entityType);
                //noinspection unchecked
                entityCreator.fromMap(entity, ((DataAccessObject) entity).getTableMetadata().getColumns(), rowHandler.handleRow(resultSet));
                //noinspection unchecked
                ((InitializedEntity<E>) entity).setOriginalCopy(entity);
                result.add(entity);
            }
        } catch (RegistryException e) {
            throw new UnrecognizedStatementError(entityType, queryName);
        } catch (SQLException e) {
            throw new StatementExecutionFailureError("Failed to execute query", e);
        }
        eventHandler.afterExecuteQuery(entityType, queryName, values, result);
        return result;
    }

    @Override
    public <E> List<E> executeQuery(E sample, String queryName) {
        eventHandler.beforeExecuteQuery(sample, queryName);
        final DataAccessObject<E, Serializable> object = checkEntity(sample);
        final Map<String, Object> map = mapCreator.toMap(object.getTableMetadata(), sample);
        final List<E> result = executeQuery(object.getTableMetadata().getEntityType(), queryName, map);
        eventHandler.afterExecuteQuery(sample, queryName, result);
        return result;
    }

    @Override
    public <O> List<O> executeQuery(O sample) {
        //noinspection unchecked
        final Class<O> resultType = (Class<O>) sample.getClass();
        final Map<String, Object> values = mapCreator.toMap(columnMappingMetadataCollector.collectMetadata(resultType), sample);
        return executeQuery(resultType, values);
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
        if (void.class.equals(annotation.targetEntity()) || annotation.query().isEmpty()) {
            throw new PartialEntityDefinitionError("Could not resolve query for partial entity " + resultType.getCanonicalName());
        }
        return executeQuery(annotation.targetEntity(), annotation.query(), resultType, values);
    }

    private <E, O> List<O> executeQuery(Class<E> entityType, String queryName, Class<O> resultType, Map<String, Object> values) {
        final List<O> list = new ArrayList<O>();
        final List<Map<String, Object>> maps = executeUntypedQuery(entityType, queryName, values);
        for (Map<String, Object> map : maps) {
            final O entity;
            try {
                entity = beanInitializer.initialize(resultType, new Class[0]);
            } catch (BeanInstantiationException e) {
                throw new EntityInitializationError(resultType, e);
            }
            entityCreator.fromMap(entity, columnMappingMetadataCollector.collectMetadata(resultType), map);
            list.add(entity);
        }
        return list;
    }

    @Override
    public <E> List<Map<String, Object>> executeUntypedQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        final ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            final Statement statement = session.getStatementRegistry().get(entityType.getCanonicalName() + "." + queryName);
            if (!StatementType.QUERY.equals(statement.getType())) {
                throw new InvalidStatementTypeError(statement.getType());
            }
            final PreparedStatement preparedStatement = statement.prepare(session.getConnection(), values);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(rowHandler.handleRow(resultSet));
            }
        } catch (RegistryException e) {
            throw new UnrecognizedStatementError(entityType, queryName);
        } catch (SQLException e) {
            throw new StatementExecutionFailureError("Failed to execute untyped query", e);
        }
        return list;
    }

    @Override
    public <O> int executeUpdate(O sample) {
        final Class<?> resultType = sample.getClass();
        if (!resultType.isAnnotationPresent(Partial.class)) {
            throw new PartialEntityDefinitionError("Expected to find @Partial on " + resultType.getCanonicalName());
        }
        final Partial annotation = resultType.getAnnotation(Partial.class);
        if (void.class.equals(annotation.targetEntity()) || annotation.query().isEmpty()) {
            throw new PartialEntityDefinitionError("Could not resolve query for partial entity " + resultType.getCanonicalName());
        }
        return executeUpdate(annotation.targetEntity(), annotation.query(), mapCreator.toMap(columnMappingMetadataCollector.collectMetadata(resultType), sample));
    }

    private <E, K extends Serializable> DataAccessObject<E, K> checkEntity(E entity) {
        Assert.assertNotNull(entity);
        if (!entityContext.has(entity)) {
            throw new EntityOutOfContextError(entity.getClass());
        }
        //noinspection unchecked
        return (DataAccessObject<E, K>) entity;
    }

    @Override
    public <E> E getInstance(Class<E> entityType) {
        return entityContext.getInstance(session.getMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> E getInstance(TableMetadata<E> tableMetadata) {
        return getInstance(tableMetadata.getEntityType());
    }

    @Override
    public <E> boolean has(E entity) {
        return entityContext.has(entity);
    }

    @Override
    public <I> void addInterface(Class<I> ifc, Class<? extends I> implementation) {
        entityContext.addInterface(ifc, implementation);
    }

    @Override
    public void addHandler(DataAccessEventHandler eventHandler) {
        this.eventHandler.addHandler(eventHandler);
    }

}
