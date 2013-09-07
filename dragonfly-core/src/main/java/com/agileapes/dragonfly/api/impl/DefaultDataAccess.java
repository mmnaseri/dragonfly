package com.agileapes.dragonfly.api.impl;

import com.agileapes.couteau.basics.assets.Assert;
import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.couteau.reflection.beans.BeanInitializer;
import com.agileapes.couteau.reflection.beans.impl.ConstructorBeanInitializer;
import com.agileapes.couteau.reflection.error.BeanInstantiationException;
import com.agileapes.dragonfly.annotations.Partial;
import com.agileapes.dragonfly.api.DataAccessObject;
import com.agileapes.dragonfly.api.PartialDataAccess;
import com.agileapes.dragonfly.entity.*;
import com.agileapes.dragonfly.entity.impl.DefaultEntityContext;
import com.agileapes.dragonfly.entity.impl.DefaultEntityMapCreator;
import com.agileapes.dragonfly.entity.impl.DefaultEntityRowHandler;
import com.agileapes.dragonfly.entity.impl.DefaultMapEntityCreator;
import com.agileapes.dragonfly.error.*;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.impl.ColumnMappingMetadataCollector;
import com.agileapes.dragonfly.statement.Statement;
import com.agileapes.dragonfly.statement.StatementType;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;
import com.agileapes.dragonfly.tools.MapTools;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 19:16)
 */
public class DefaultDataAccess implements PartialDataAccess {

    private final DataSource dataSource;
    private final StatementRegistry statementRegistry;
    private final EntityContext entityContext;
    private final MetadataRegistry metadataRegistry;
    private final EntityRowHandler rowHandler;
    private final MapEntityCreator entityCreator;
    private final EntityMapCreator mapCreator;
    private final BeanInitializer beanInitializer;
    private final ColumnMappingMetadataCollector columnMappingMetadataCollector;

    public DefaultDataAccess(DataSource dataSource, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry) {
        this.dataSource = dataSource;
        this.statementRegistry = statementRegistry;
        this.metadataRegistry = metadataRegistry;
        this.entityContext = new DefaultEntityContext(this);
        this.rowHandler = new DefaultEntityRowHandler();
        this.entityCreator = new DefaultMapEntityCreator(entityContext);
        this.mapCreator = new DefaultEntityMapCreator();
        beanInitializer = new ConstructorBeanInitializer();
        columnMappingMetadataCollector = new ColumnMappingMetadataCollector();
    }

    @Override
    public <E> void save(E entity) {
        final DataAccessObject<E, Serializable> object = checkEntity(entity);
        //noinspection unchecked
        int affectedRows = 0;
        try {
            final boolean shouldUpdate = (object.hasKey() && object.accessKey() != null) || ((InitializedEntity) object).isDirtied() && find(entity).size() == 1;//object.hasKey() && object.accessKey() != null;
            if (shouldUpdate) {
                //noinspection unchecked
                affectedRows = internalExecuteUpdate(((InitializedEntity<E>) object).getOriginalCopy(), entity, "updateBySample");
            } else {
                affectedRows = internalExecuteUpdate(entity, "insert", true);
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        if (affectedRows <= 0) {
            throw new UnsuccessfulOperationError("Failed to save entity " + object.getQualifiedName());
        }
    }

    @Override
    public <E> void delete(E entity) {
        if (internalExecuteUpdate(entity, "deleteLike", true) <= 0) {
            throw new UnsupportedOperationException("Failed to delete entity");
        }
    }

    @Override
    public <E, K extends Serializable> void delete(Class<E> entityType, K key) {
        final E entity = getInstance(entityType);
        //noinspection unchecked
        final DataAccessObject<E, K> object = (DataAccessObject<E, K>) entity;
        object.changeKey(key);
        delete(entity);
    }

    @Override
    public <E> void deleteAll(Class<E> entityType) {
        if (internalExecuteUpdate(entityType, "deleteAll", true) <= 0) {
            throw new UnsupportedOperationException("Failed to delete entity");
        }
    }

    @Override
    public <E> List<E> find(E sample) {
        return internalExecuteQuery(sample, "findLike");
    }

    @Override
    public <E, K extends Serializable> E find(Class<E> entityType, K key) {
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
        return result.get(0);
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType) {
        return executeQuery(entityType, "findAll", Collections.<String, Object>emptyMap());
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
        try {
            return statementRegistry.get(entityType.getCanonicalName() + "." + queryName).prepare(dataSource.getConnection(), values).executeUpdate();
        } catch (RegistryException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public <E> int executeUpdate(E sample, String queryName) {
        return internalExecuteUpdate(sample, queryName, "");
    }

    private <E> int internalExecuteUpdate(E sample, String queryName, boolean prefix) {
        final DataAccessObject<E, Serializable> object = checkEntity(sample);
        final Map<String, Object> map = new HashMap<String, Object>();
        if (prefix) {
            map.putAll(MapTools.prefixKeys(mapCreator.toMap(object.getTableMetadata(), sample), "value."));
        } else {
            map.putAll(mapCreator.toMap(object.getTableMetadata(), sample));
        }
        try {
            final Statement statement = statementRegistry.get(object.getQualifiedName() + "." + queryName);
            final PreparedStatement preparedStatement = statement.prepare(dataSource.getConnection(), map);
            final int affectedRows = preparedStatement.executeUpdate();
            if (StatementType.INSERT.equals(statement.getType())) {
                final ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    object.changeKey((Serializable) generatedKeys.getObject(1));
                } else {
                    throw new UnsupportedOperationException("Failed to obtain generated key values for the entity");
                }
            }
            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (RegistryException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public <E> List<E> executeQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        try {
            final TableMetadata<E> tableMetadata = metadataRegistry.getTableMetadata(entityType);
            final Statement statement = statementRegistry.get(entityType.getCanonicalName() + "." + queryName);
            final PreparedStatement preparedStatement = statement.prepare(dataSource.getConnection(), values);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final ArrayList<E> result = new ArrayList<E>();
            while (resultSet.next()) {
                final E entity = entityCreator.fromMap(tableMetadata, rowHandler.handleRow(resultSet));
                //noinspection unchecked
                ((InitializedEntity<E>) entity).setOriginalCopy(entity);
                result.add(entity);
            }
            return result;
        } catch (RegistryException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <E> List<E> executeQuery(E sample, String queryName) {
        final DataAccessObject<E, Serializable> object = checkEntity(sample);
        final Map<String, Object> map = mapCreator.toMap(object.getTableMetadata(), sample);
        return executeQuery(object.getTableMetadata().getEntityType(), queryName, map);
    }

    @Override
    public <O> List<O> executePartialQuery(O sample) {
        //noinspection unchecked
        final Class<O> resultType = (Class<O>) sample.getClass();
        final Map<String, Object> values = mapCreator.toMap(columnMappingMetadataCollector.collectMetadata(resultType), sample);
        return executePartialQuery(resultType, values);
    }

    @Override
    public <O> List<O> executePartialQuery(Class<O> resultType) {
        return executePartialQuery(resultType, Collections.<String, Object>emptyMap());
    }

    @Override
    public <O> List<O> executePartialQuery(Class<O> resultType, Map<String, Object> values) {
        if (!resultType.isAnnotationPresent(Partial.class)) {
            throw new PartialEntityDefinitionError("Expected to find @Partial on " + resultType.getCanonicalName());
        }
        final Partial annotation = resultType.getAnnotation(Partial.class);
        if (void.class.equals(annotation.targetEntity()) || annotation.query().isEmpty()) {
            throw new PartialEntityDefinitionError("Could not resolve query for partial entity " + resultType.getCanonicalName());
        }
        return executePartialQuery(annotation.targetEntity(), annotation.query(), resultType, values);
    }

    @Override
    public <E, O> List<O> executePartialQuery(Class<E> entityType, String queryName, Class<O> resultType, Map<String, Object> values) {
        final List<O> list = new ArrayList<O>();
        final List<Map<String, Object>> maps = executePartialQuery(entityType, queryName, values);
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
    public <E> List<Map<String, Object>> executePartialQuery(Class<E> entityType, String queryName, Map<String, Object> values) {
        final ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            final Statement statement = statementRegistry.get(entityType.getCanonicalName() + "." + queryName);
            if (!StatementType.QUERY.equals(statement.getType())) {
                throw new UnsupportedStatementTypeError(statement.getType());
            }
            final PreparedStatement preparedStatement = statement.prepare(dataSource.getConnection(), values);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(rowHandler.handleRow(resultSet));
            }
        } catch (RegistryException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public <O> int executePartialUpdate(O sample) {
        final Class<?> resultType = sample.getClass();
        if (!resultType.isAnnotationPresent(Partial.class)) {
            throw new PartialEntityDefinitionError("Expected to find @Partial on " + resultType.getCanonicalName());
        }
        final Partial annotation = resultType.getAnnotation(Partial.class);
        if (void.class.equals(annotation.targetEntity()) || annotation.query().isEmpty()) {
            throw new PartialEntityDefinitionError("Could not resolve query for partial entity " + resultType.getCanonicalName());
        }
        return executePartialUpdate(annotation.targetEntity(), annotation.query(), mapCreator.toMap(columnMappingMetadataCollector.collectMetadata(resultType), sample));
    }

    @Override
    public <E> int executePartialUpdate(Class<E> entityType, String queryName, Map<String, Object> values) {
        try {
            final Statement statement = statementRegistry.get(entityType.getCanonicalName() + "." + queryName);
            final PreparedStatement preparedStatement = statement.prepare(dataSource.getConnection(), values);
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public <E> int executePartialUpdate(Class<E> entityType, String queryName) {
        return executePartialUpdate(entityType, queryName, Collections.<String, Object>emptyMap());
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
        return entityContext.getInstance(metadataRegistry.getTableMetadata(entityType));
    }

    @Override
    public <E> E getInstance(TableMetadata<E> tableMetadata) {
        return getInstance(tableMetadata.getEntityType());
    }

    @Override
    public <E> boolean has(E entity) {
        return entityContext.has(entity);
    }

}
