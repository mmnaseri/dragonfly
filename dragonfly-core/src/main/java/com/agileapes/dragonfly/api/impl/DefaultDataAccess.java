package com.agileapes.dragonfly.api.impl;

import com.agileapes.couteau.basics.assets.Assert;
import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.dragonfly.api.DataAccess;
import com.agileapes.dragonfly.api.DataAccessObject;
import com.agileapes.dragonfly.entity.*;
import com.agileapes.dragonfly.entity.impl.*;
import com.agileapes.dragonfly.error.EntityOutOfContextError;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.TableMetadataAware;
import com.agileapes.dragonfly.statement.Statement;
import com.agileapes.dragonfly.statement.impl.StatementRegistry;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 19:16)
 */
public class DefaultDataAccess implements DataAccess {

    private final DataSource dataSource;
    private final StatementRegistry statementRegistry;
    private final EntityContext entityContext;
    private final MetadataRegistry metadataRegistry;
    private final EntityRowHandler rowHandler;
    private final MapEntityCreator entityCreator;
    private final EntityMapCreator mapCreator;

    public DefaultDataAccess(DataSource dataSource, StatementRegistry statementRegistry, MetadataRegistry metadataRegistry) {
        this.dataSource = dataSource;
        this.statementRegistry = statementRegistry;
        this.metadataRegistry = metadataRegistry;
        this.entityContext = new DefaultEntityContext(this);
        this.rowHandler = new DefaultEntityRowHandler();
        this.entityCreator = new DefaultMapEntityCreator(entityContext);
        this.mapCreator = new DefaultEntityMapCreator();
    }

    @Override
    public <E> void save(E entity) {
        final DataAccessObject<Serializable> object = checkEntity(entity);
        //noinspection unchecked
        final TableMetadata<E> tableMetadata = metadataRegistry.getTableMetadata(((TableMetadataAware<E>) entity).getTableMetadata().getEntityType());
        try {
            final boolean shouldUpdate = (object.hasKey() && object.accessKey() != null) || find(entity).size() == 1;//object.hasKey() && object.accessKey() != null;
            if (shouldUpdate) {
                //noinspection unchecked
                statementRegistry.get(object.getQualifiedName() + ".updateBySample").prepare(dataSource.getConnection(), ((InitializedEntity<E>) entity).getOriginalCopy(), entity).executeUpdate();
            } else {
                final PreparedStatement statement = statementRegistry.get(object.getQualifiedName() + ".insert").prepare(dataSource.getConnection(), entity);
                statement.executeUpdate();
                final ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    object.changeKey((Serializable) generatedKeys.getObject(1));
                }
            }
            //noinspection unchecked
            ((InitializedEntity<E>) entity).setOriginalCopy(entityCreator.fromMap(tableMetadata, mapCreator.toMap(tableMetadata, entity)));
        } catch (RegistryException ignored) {
            ignored.printStackTrace();
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
    }

    @Override
    public <E> void delete(E entity) {
        final DataAccessObject<Serializable> object = checkEntity(entity);
        try {
            statementRegistry.get(object.getQualifiedName() + ".deleteLike").prepare(dataSource.getConnection(), entity).executeUpdate();
        } catch (RegistryException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <E, K extends Serializable> void delete(Class<E> entityType, K key) {
        try {
            final Statement statement = statementRegistry.get(entityType.getCanonicalName() + ".deleteByKey");
            //noinspection unchecked
            final DataAccessObject<K> object = (DataAccessObject<K>) getInstance(entityType);
            object.changeKey(key);
            statement.prepare(dataSource.getConnection(), object).executeUpdate();
        } catch (RegistryException ignored) {
        } catch (SQLException ignored) {
        }
    }

    @Override
    public <E> void deleteAll(Class<E> entityType) {
        try {
            statementRegistry.get(entityType.getCanonicalName() + ".deleteAll").prepare(dataSource.getConnection()).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (RegistryException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <E> List<E> find(E sample) {
        final DataAccessObject<Serializable> object = checkEntity(sample);
        final ArrayList<E> result = new ArrayList<E>();
        try {
            //noinspection unchecked
            final TableMetadata<E> tableMetadata = ((TableMetadataAware<E>) object).getTableMetadata();
            final PreparedStatement statement = statementRegistry.get(object.getQualifiedName() + ".findLike").prepare(dataSource.getConnection(), sample);
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final Map<String, Object> map = rowHandler.handleRow(tableMetadata, resultSet);
                final E entity = entityCreator.fromMap(tableMetadata, map);
                result.add(entity);
            }
        } catch (RegistryException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public <E, K extends Serializable> E find(Class<E> entityType, K key) {
        try {
            final TableMetadata<E> tableMetadata = metadataRegistry.getTableMetadata(entityType);
            final Statement statement = statementRegistry.get(entityType.getCanonicalName() + ".findByKey");
            //noinspection unchecked
            final DataAccessObject<K> object = (DataAccessObject<K>) getInstance(entityType);
            object.changeKey(key);
            final ResultSet resultSet = statement.prepare(dataSource.getConnection(), object).executeQuery();
            resultSet.next();
            final Map<String, Object> map = rowHandler.handleRow(tableMetadata, resultSet);
            final E result = entityCreator.fromMap(tableMetadata, map);
            //noinspection unchecked
            ((InitializedEntity<E>) result).setOriginalCopy(entityCreator.fromMap(tableMetadata, map));
            return result;
        } catch (RegistryException ignored) {
            ignored.printStackTrace();
            return null;
        } catch (SQLException ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    @Override
    public <E> List<E> findAll(Class<E> entityType) {
        final TableMetadata<E> tableMetadata = metadataRegistry.getTableMetadata(entityType);
        final Statement statement;
        try {
            statement = statementRegistry.get(entityType.getCanonicalName() + ".findAll");
        } catch (RegistryException ignored) {
            return null;
        }
        final PreparedStatement preparedStatement;
        final ArrayList<E> result = new ArrayList<E>();
        try {
            preparedStatement = statement.prepare(dataSource.getConnection());
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Map<String,Object> map = rowHandler.handleRow(tableMetadata, resultSet);
                result.add(entityCreator.fromMap(tableMetadata, map));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public <E, K extends Serializable> K getKey(E entity) {
        //noinspection unchecked
        return (K) checkEntity(entity).accessKey();
    }

    private DataAccessObject<Serializable> checkEntity(Object entity) {
        Assert.assertNotNull(entity);
        if (!entityContext.has(entity)) {
            throw new EntityOutOfContextError(entity.getClass());
        }
        //noinspection unchecked
        return (DataAccessObject<Serializable>) entity;
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
