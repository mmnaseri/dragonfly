package com.agileapes.dragonfly.data.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.assets.Assert;
import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.couteau.reflection.beans.BeanInitializer;
import com.agileapes.couteau.reflection.beans.impl.ConstructorBeanInitializer;
import com.agileapes.couteau.reflection.error.BeanInstantiationException;
import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.dragonfly.annotations.ParameterMode;
import com.agileapes.dragonfly.annotations.Partial;
import com.agileapes.dragonfly.data.*;
import com.agileapes.dragonfly.entity.*;
import com.agileapes.dragonfly.entity.impl.DefaultEntityContext;
import com.agileapes.dragonfly.entity.impl.DefaultEntityMapCreator;
import com.agileapes.dragonfly.entity.impl.DefaultMapEntityCreator;
import com.agileapes.dragonfly.entity.impl.DefaultRowHandler;
import com.agileapes.dragonfly.error.*;
import com.agileapes.dragonfly.events.DataAccessEventHandler;
import com.agileapes.dragonfly.events.EventHandlerContext;
import com.agileapes.dragonfly.events.impl.CompositeDataAccessEventHandler;
import com.agileapes.dragonfly.metadata.ParameterMetadata;
import com.agileapes.dragonfly.metadata.StoredProcedureMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.impl.ColumnMappingMetadataCollector;
import com.agileapes.dragonfly.security.DataSecurityManager;
import com.agileapes.dragonfly.security.impl.StoredProcedureSubject;
import com.agileapes.dragonfly.statement.Statement;
import com.agileapes.dragonfly.statement.StatementType;
import com.agileapes.dragonfly.statement.impl.ProcedureCallStatement;
import com.agileapes.dragonfly.tools.MapTools;
import freemarker.template.utility.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * <p>This is the default implementation of the {@link com.agileapes.dragonfly.data.DataAccess}
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

    private static final Log log = LogFactory.getLog(DataAccess.class);
    private final DataAccessSession session;
    private final ModifiableEntityContext entityContext;
    private final RowHandler rowHandler;
    private final MapEntityCreator entityCreator;
    private final EntityMapCreator mapCreator;
    private final BeanInitializer beanInitializer;
    private final ColumnMappingMetadataCollector columnMappingMetadataCollector;
    private final CompositeDataAccessEventHandler eventHandler;
    private final DataSecurityManager securityManager;

    public DefaultDataAccess(DataAccessSession session, DataSecurityManager securityManager) {
        this(session, securityManager, true);
    }

    public DefaultDataAccess(DataAccessSession session, DataSecurityManager securityManager, boolean autoInitialize) {
        this.session = session;
        this.securityManager = securityManager;
        if (autoInitialize) {
            log.info("Automatically initializing the session");
            synchronized (this.session) {

                if (!this.session.isInitialized()) {
                    this.session.initialize();
                }
            }
        }
        this.entityContext = new DefaultEntityContext(this, securityManager);
        this.rowHandler = new DefaultRowHandler();
        this.entityCreator = new DefaultMapEntityCreator(entityContext);
        this.mapCreator = new DefaultEntityMapCreator();
        beanInitializer = new ConstructorBeanInitializer();
        columnMappingMetadataCollector = new ColumnMappingMetadataCollector();
        eventHandler = new CompositeDataAccessEventHandler();
    }

    @Override
    public <E> void save(E entity) {
        //noinspection unchecked
        entity = (E) checkEntity(entity);
        log.info("Going to save entity " + entity);
        eventHandler.beforeSave(entity);
        final DataAccessObject<E, Serializable> object = checkEntity(entity);
        //noinspection unchecked
        int affectedRows;
        final boolean shouldUpdate;
        try {
            shouldUpdate = (object.hasKey() && object.accessKey() != null) || ((InitializedEntity) object).isDirtied() && find(entity).size() == 1;
            if (shouldUpdate) {
                log.info("Updating existing entity on the database");
                eventHandler.beforeUpdate(entity);
                //noinspection unchecked
                affectedRows = internalExecuteUpdate(((InitializedEntity<E>) object).getOriginalCopy(), entity, "updateBySample");
            } else {
                log.info("Inserting entity into the database");
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
        //noinspection unchecked
        entity = (E) checkEntity(entity);
        log.info("Going to delete entity " + entity);
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
        log.warn("Going to delete all entities of type " + entityType);
        eventHandler.beforeDeleteAll(entityType);
        internalExecuteUpdate(getInstance(entityType), "deleteAll", true);
        eventHandler.afterDeleteAll(entityType);
    }

    @Override
    public <E> void truncate(Class<E> entityType) {
        log.warn("Going to truncate data and metadata for " + entityType);
        log.warn("This action is not transactional and cannot be undone");
        eventHandler.beforeTruncate(entityType);
        executeUpdate(entityType, "truncate", Collections.<String, Object>emptyMap());
        eventHandler.afterTruncate(entityType);
    }

    @Override
    public <E> List<E> find(E sample) {
        //noinspection unchecked
        sample = (E) checkEntity(sample);
        log.info("Looking entities matching " + sample);
        eventHandler.beforeFind(sample);
        final List<E> list = internalExecuteQuery(sample, "findLike");
        eventHandler.afterFind(sample, list);
        return list;
    }

    @Override
    public <E, K extends Serializable> E find(Class<E> entityType, K key) {
        eventHandler.beforeFind(entityType, key);
        log.info("Looking for entity of type " + entityType + " with key " + key);
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
        log.info("Looking up all entities of type " + entityType);
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
        final Map<String, Object> newMap = mapCreator.toMap(object.getTableMetadata(), replacement);
        for (Map.Entry<String, Object> entry : newMap.entrySet()) {
            if (!map.containsKey("value." + entry.getKey())) {
                map.put("value." + entry.getKey(), entry.getValue());
            }
        }
        map.putAll(MapTools.prefixKeys(newMap, "new."));
        return executeUpdate(object.getTableMetadata().getEntityType(), queryName, map);
    }

    private <E> List<E> internalExecuteQuery(E sample, String queryName) {
        final DataAccessObject<E, Serializable> object = checkEntity(sample);
        return executeQuery(object.getTableMetadata().getEntityType(), queryName, MapTools.prefixKeys(mapCreator.toMap(object.getTableMetadata(), sample), "value."));
    }

    @Override
    public <E> int executeUpdate(Class<E> entityType, String queryName, Map<String, Object> values) {
        log.info("Executing update query " + entityType.getCanonicalName() + "." + queryName);
        eventHandler.beforeExecuteUpdate(entityType, queryName, values);
        final int affectedRows;
        try {
            final Statement statement = session.getStatementRegistry().get(entityType.getCanonicalName() + "." + queryName);
            if (StatementType.DEFINITION.equals(statement.getType()) || StatementType.QUERY.equals(statement.getType())) {
                throw new InvalidStatementTypeError(statement.getType());
            }
            affectedRows = statement.prepare(session.getConnection(), values).executeUpdate();
        } catch (RegistryException e) {
            throw new UnrecognizedQueryError(entityType, queryName);
        } catch (SQLException e) {
            throw new StatementExecutionFailureError("Failed to execute update statement " + queryName, e);
        }
        return affectedRows;
    }

    @Override
    public <E> int executeUpdate(E sample, String queryName) {
        //noinspection unchecked
        sample = (E) checkEntity(sample);
        log.info("Executing update query " + sample.getClass().getCanonicalName() + "." + queryName);
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
            throw new UnrecognizedQueryError(object.getTableMetadata().getEntityType(), queryName);
        }
        return affectedRows;
    }

    @Override
    public <E> List<E> executeQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        log.info("Fetching result for query " + entityType.getCanonicalName() + "." + queryName);
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
                ((InitializedEntity<E>) entity).freeze();
                //noinspection unchecked
                entityCreator.fromMap(entity, ((DataAccessObject) entity).getTableMetadata().getColumns(), rowHandler.handleRow(resultSet));
                //noinspection unchecked
                ((InitializedEntity<E>) entity).setOriginalCopy(entity);
                //noinspection unchecked
                ((DataAccessObject) entity).loadRelations();
                //noinspection unchecked
                ((InitializedEntity<E>) entity).unfreeze();
                result.add(entity);
            }
        } catch (RegistryException e) {
            throw new UnrecognizedQueryError(entityType, queryName);
        } catch (SQLException e) {
            throw new StatementExecutionFailureError("Failed to execute query", e);
        }
        eventHandler.afterExecuteQuery(entityType, queryName, values, result);
        return result;
    }

    @Override
    public <E> List<E> executeQuery(E sample, String queryName) {
        log.info("Fetching result for query " + sample.getClass().getCanonicalName() + "." + queryName);
        //noinspection unchecked
        sample = (E) checkEntity(sample);
        eventHandler.beforeExecuteQuery(sample, queryName);
        final DataAccessObject<E, Serializable> object = checkEntity(sample);
        final Map<String, Object> map = mapCreator.toMap(object.getTableMetadata(), sample);
        final List<E> result = executeQuery(object.getTableMetadata().getEntityType(), queryName, map);
        eventHandler.afterExecuteQuery(sample, queryName, result);
        return result;
    }

    @Override
    public <E> List<?> call(Class<E> entityType, final String procedureName, Object... parameters) {
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
        final ProcedureCallStatement statement;
        try {
            statement = (ProcedureCallStatement) session.getStatementRegistry().get(entityType.getCanonicalName() + ".call" + StringUtil.capitalize(procedureName));
        } catch (RegistryException e) {
            throw new UnrecognizedProcedureError(entityType, procedureName);
        }
        securityManager.checkAccess(new StoredProcedureSubject(procedureMetadata, parameters));
        final Map<String, Object> values = new HashMap<String, Object>();
        for (int i = 0; i < parameters.length; i++) {
            values.put("value.parameter" + i, parameters[i] instanceof Reference ? ((Reference<?>) parameters[i]).getValue() : parameters[i]);
        }
        final CallableStatement callableStatement;
        final ArrayList<Object> result = new ArrayList<Object>();
        try {
            callableStatement = statement.prepare(session.getConnection(), values);
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
                final TableMetadata<?> resultTableMetadata;
                if (procedureMetadata.isPartial()) {
                    resultTableMetadata = null;
                } else {
                    resultTableMetadata = session.getMetadataRegistry().getTableMetadata(procedureMetadata.getResultType());
                }
                while (resultSet.next()) {
                    final Map<String, Object> map = rowHandler.handleRow(resultSet);
                    if (resultTableMetadata == null) {
                        try {
                            result.add(entityCreator.fromMap(beanInitializer.initialize(procedureMetadata.getResultType(), new Class[0]), columnMappingMetadataCollector.collectMetadata(procedureMetadata.getResultType()), map));
                        } catch (BeanInstantiationException e) {
                            throw new EntityInitializationError(procedureMetadata.getResultType(), e);
                        }
                    } else {
                        result.add(entityCreator.fromMap(resultTableMetadata, map));
                    }
                }
            }
            for (int i = 0; i < procedureMetadata.getParameters().size(); i++) {
                ParameterMetadata metadata = procedureMetadata.getParameters().get(i);
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
            throw new UnrecognizedQueryError(entityType, queryName);
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
            //noinspection unchecked
            final TableMetadata<E> tableMetadata = (TableMetadata<E>) session.getMetadataRegistry().getTableMetadata(entity.getClass());
            final Map<String, Object> map = mapCreator.toMap(tableMetadata, entity);
            final E instance = entityCreator.fromMap(getInstance(tableMetadata), tableMetadata.getColumns(), map);
            //noinspection unchecked
            return (DataAccessObject<E, K>) instance;
        }
        //noinspection unchecked
        return (DataAccessObject<E, K>) entity;
    }

    @Override
    public <E> E getInstance(Class<E> entityType) {
        log.info("Dispensing enhanced instance for " + entityType.getCanonicalName());
        final E instance = entityContext.getInstance(session.getMetadataRegistry().getTableMetadata(entityType));
        ((InitializedEntity) instance).unfreeze();
        return instance;
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
        log.info("Registering enhancing interface " + ifc.getCanonicalName() + " with the context");
        entityContext.addInterface(ifc, implementation);
    }

    @Override
    public void addHandler(DataAccessEventHandler eventHandler) {
        log.info("Registering event handler: " + eventHandler);
        this.eventHandler.addHandler(eventHandler);
    }

}
