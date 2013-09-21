package com.agileapes.dragonfly.data.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.basics.assets.Assert;
import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.couteau.reflection.beans.BeanAccessor;
import com.agileapes.couteau.reflection.beans.BeanInitializer;
import com.agileapes.couteau.reflection.beans.BeanWrapper;
import com.agileapes.couteau.reflection.beans.impl.ConstructorBeanInitializer;
import com.agileapes.couteau.reflection.beans.impl.MethodBeanAccessor;
import com.agileapes.couteau.reflection.beans.impl.MethodBeanWrapper;
import com.agileapes.couteau.reflection.error.BeanInstantiationException;
import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.dragonfly.annotations.ParameterMode;
import com.agileapes.dragonfly.annotations.Partial;
import com.agileapes.dragonfly.data.*;
import com.agileapes.dragonfly.entity.*;
import com.agileapes.dragonfly.entity.impl.DefaultEntityContext;
import com.agileapes.dragonfly.entity.impl.DefaultEntityHandlerContext;
import com.agileapes.dragonfly.entity.impl.DefaultEntityInitializationContext;
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
public class DefaultDataAccess implements PartialDataAccess, EventHandlerContext {

    private static final Log log = LogFactory.getLog(DataAccess.class);
    private final DataAccessSession session;
    private final EntityContext entityContext;
    private final RowHandler rowHandler;
    private final EntityHandlerContext handlerContext;
    private final BeanInitializer beanInitializer;
    private final ColumnMappingMetadataCollector columnMappingMetadataCollector;
    private final CompositeDataAccessEventHandler eventHandler;
    private final DataSecurityManager securityManager;

    public DefaultDataAccess(DataAccessSession session, DataSecurityManager securityManager, EntityContext entityContext, DefaultEntityHandlerContext handlerContext) {
        this(session, securityManager, entityContext, handlerContext, true);
    }

    public DefaultDataAccess(DataAccessSession session, DataSecurityManager securityManager, EntityContext entityContext, EntityHandlerContext handlerContext, boolean autoInitialize) {
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
        this.rowHandler = new DefaultRowHandler();
        beanInitializer = new ConstructorBeanInitializer();
        columnMappingMetadataCollector = new ColumnMappingMetadataCollector();
        eventHandler = new CompositeDataAccessEventHandler();
        this.handlerContext = handlerContext;
        if (entityContext instanceof DefaultEntityContext) {
            DefaultEntityContext context = (DefaultEntityContext) entityContext;
            context.setDataAccess(this);
        }
        this.entityContext = entityContext;
    }

    @Override
    public <E> void save(E entity) {
        boolean needsReflection = !has(entity);
        E original = entity;
        //noinspection unchecked
        entity = (E) checkEntity(entity);
        log.info("Going to save entity " + entity);
        eventHandler.beforeSave(entity);
        //noinspection unchecked
        final DataAccessObject<E, Serializable> object = (DataAccessObject<E, Serializable>) entity;
        int affectedRows;
        final boolean shouldUpdate;
        final EntityHandler<E> entityHandler = handlerContext.getHandler(entity);
        try {
            shouldUpdate = (entityHandler.hasKey() && entityHandler.getKey(entity) != null) || ((InitializedEntity) object).isDirtied() && find(entity).size() == 1;
            if (shouldUpdate) {
                log.info("Updating existing entity on the database");
                eventHandler.beforeUpdate(entity);
                //noinspection unchecked
                affectedRows = internalExecuteUpdate(((InitializedEntity<E>) object).getOriginalCopy(), entity, "updateBySample");
            } else {
                log.info("Inserting entity into the database");
                eventHandler.beforeInsert(entity);
                affectedRows = internalExecuteUpdate(entity, "insert", true);
                if (affectedRows > 0 && needsReflection) {
                    entityHandler.setKey(original, entityHandler.getKey(entity));
                }
            }
        } catch (Exception e) {
            throw new StatementExecutionFailureError("Failed to execute save statement", e);
        }
        if (affectedRows <= 0) {
            throw new UnsuccessfulOperationError("Failed to save entity " + entityHandler.getEntityType().getCanonicalName());
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
        handlerContext.getHandler(entityType).setKey(entity, key);
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
        handlerContext.getHandler(entityType).setKey(entity, key);
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

    private <E> int internalExecuteUpdate(E original, E replacement, String queryName) {
        final EntityHandler<E> entityHandler = handlerContext.getHandler(original);
        final Map<String, Object> originalMap = entityHandler.toMap(original);
        final Map<String, Object> map = MapTools.prefixKeys(originalMap, "value.");
        map.putAll(MapTools.prefixKeys(originalMap, "old."));
        final Map<String, Object> newMap = entityHandler.toMap(replacement);
        for (Map.Entry<String, Object> entry : newMap.entrySet()) {
            if (!map.containsKey("value." + entry.getKey())) {
                map.put("value." + entry.getKey(), entry.getValue());
            }
        }
        map.putAll(MapTools.prefixKeys(newMap, "new."));
        return executeUpdate(entityHandler.getEntityType(), queryName, map);
    }

    private <E> List<E> internalExecuteQuery(E sample, String queryName) {
        final DataAccessObject<E, Serializable> object = checkEntity(sample);
        final EntityHandler<E> entityHandler = handlerContext.getHandler(sample);
        ((InitializedEntity) object).freeze();
        //noinspection unchecked
        final List<E> list = executeQuery(entityHandler.getEntityType(), queryName, MapTools.prefixKeys(entityHandler.toMap((E) object), "value."));
        ((InitializedEntity) object).unfreeze();
        return list;
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
            affectedRows = statement.prepare(session.getConnection(), handlerContext, values).executeUpdate();
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
        final InitializedEntity entity = (InitializedEntity) checkEntity(sample);
        final Map<String, Object> map = new HashMap<String, Object>();
        final EntityHandler<E> entityHandler = handlerContext.getHandler(sample);
        entity.freeze();
        if (prefix) {
            map.putAll(MapTools.prefixKeys(entityHandler.toMap(sample), "value."));
        } else {
            map.putAll(entityHandler.toMap(sample));
        }
        entity.unfreeze();
        final int affectedRows;
        try {
            final Statement statement = session.getStatementRegistry().get(entityHandler.getEntityType().getCanonicalName() + "." + queryName);
            final PreparedStatement preparedStatement = statement.prepare(session.getConnection(), handlerContext, map);
            affectedRows = preparedStatement.executeUpdate();
            if (StatementType.INSERT.equals(statement.getType()) && entityHandler.hasKey() && entityHandler.isKeyAutoGenerated()) {
                if (affectedRows <= 0) {
                    throw new UnsuccessfulOperationError("Failed to insert item");
                }
                final ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                final Serializable key = session.getDatabaseDialect().retrieveKey(generatedKeys, session.getMetadataRegistry().getTableMetadata(entityHandler.getEntityType()));
                if (key == null) {
                    throw new UnsuccessfulOperationError("Failed to obtain generated key values for the entity");
                }
                entityHandler.setKey(sample, key);
            }
        } catch (SQLException e) {
            throw new StatementExecutionFailureError("Failed to execute update", e);
        } catch (RegistryException e) {
            throw new UnrecognizedQueryError(entityHandler.getEntityType(), queryName);
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
            final PreparedStatement preparedStatement = statement.prepare(session.getConnection(), handlerContext, values);
            final EntityHandler<E> entityHandler = handlerContext.getHandler(entityType);
            final ResultSet resultSet = preparedStatement.executeQuery();
            result = new ArrayList<E>();
            while (resultSet.next()) {
                E entity = getInstance(entityType);
                //noinspection unchecked
                ((InitializedEntity<E>) entity).freeze();
                entity = entityHandler.fromMap(entity, rowHandler.handleRow(resultSet), new DefaultEntityInitializationContext(this));
                //noinspection unchecked
                ((InitializedEntity<E>) entity).setOriginalCopy(entity);
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
        final EntityHandler<E> entityHandler = handlerContext.getHandler(sample);
        final Map<String, Object> map = entityHandler.toMap(sample);
        final List<E> result = executeQuery(entityHandler.getEntityType(), queryName, map);
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
            callableStatement = statement.prepare(session.getConnection(), handlerContext, values);
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
                    entityHandler = (EntityHandler<Object>) handlerContext.getHandler(procedureMetadata.getResultType());
                }
                while (resultSet.next()) {
                    final Map<String, Object> map = rowHandler.handleRow(resultSet);
                    if (entityHandler == null) {
                        try {
                            result.add(handlerContext.fromMap(beanInitializer.initialize(procedureMetadata.getResultType(), new Class[0]), columnMappingMetadataCollector.collectMetadata(procedureMetadata.getResultType()), map, null));
                        } catch (BeanInstantiationException e) {
                            throw new EntityInitializationError(procedureMetadata.getResultType(), e);
                        }
                    } else {
                        final Object instance = entityContext.getInstance(entityHandler.getEntityType());
                        result.add(entityHandler.fromMap(instance, map, new DefaultEntityInitializationContext(this)));
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
        final Map<String, Object> values = handlerContext.toMap(columnMappingMetadataCollector.collectMetadata(resultType), sample);
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
            handlerContext.fromMap(entity, columnMappingMetadataCollector.collectMetadata(resultType), map, new DefaultEntityInitializationContext(this));
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
            final PreparedStatement preparedStatement = statement.prepare(session.getConnection(), handlerContext, values);
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
        return executeUpdate(annotation.targetEntity(), annotation.query(), handlerContext.toMap(columnMappingMetadataCollector.collectMetadata(resultType), sample));
    }

    private <E, K extends Serializable> DataAccessObject<E, K> checkEntity(final E entity) {
        Assert.assertNotNull(entity);
        if (!entityContext.has(entity)) {
            //noinspection unchecked
            final TableMetadata<E> tableMetadata = (TableMetadata<E>) session.getMetadataRegistry().getTableMetadata(entity.getClass());
            final E instance = getInstance(tableMetadata);
            final BeanAccessor<E> accessor = new MethodBeanAccessor<E>(entity);
            final BeanWrapper<E> wrapper = new MethodBeanWrapper<E>(instance);
            with(accessor.getPropertyNames()).each(new Processor<String>() {
                @Override
                public void process(String propertyName) {
                    try {
                        if (!wrapper.isWritable(propertyName)) {
                            return;
                        }
                        final Object propertyValue = accessor.getPropertyValue(propertyName);
                        if (propertyValue != null) {
                            wrapper.setPropertyValue(propertyName, propertyValue);
                        }
                    } catch (Exception e) {
                        throw new EntityInitializationError(entity.getClass(), e);
                    }
                }
            });
            //noinspection unchecked
            return (DataAccessObject<E, K>) instance;
        }
        //noinspection unchecked
        return (DataAccessObject<E, K>) entity;
    }

    private <E> E getInstance(Class<E> entityType) {
        log.info("Dispensing enhanced instance for " + entityType.getCanonicalName());
        final E instance = entityContext.getInstance(session.getMetadataRegistry().getTableMetadata(entityType));
        ((InitializedEntity) instance).unfreeze();
        return instance;
    }

    private <E> E getInstance(TableMetadata<E> tableMetadata) {
        return getInstance(tableMetadata.getEntityType());
    }

    private <E> boolean has(E entity) {
        return entityContext.has(entity);
    }

    @Override
    public void addHandler(DataAccessEventHandler eventHandler) {
        log.info("Registering event handler: " + eventHandler);
        this.eventHandler.addHandler(eventHandler);
    }

}
