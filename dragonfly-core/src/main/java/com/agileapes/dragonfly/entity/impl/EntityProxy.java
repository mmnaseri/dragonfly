package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.enhancer.api.MethodDescriptor;
import com.agileapes.couteau.enhancer.api.MethodProxy;
import com.agileapes.couteau.reflection.beans.BeanWrapper;
import com.agileapes.couteau.reflection.beans.impl.MethodBeanAccessor;
import com.agileapes.couteau.reflection.beans.impl.MethodBeanWrapper;
import com.agileapes.couteau.reflection.error.NoSuchPropertyException;
import com.agileapes.couteau.reflection.error.PropertyAccessException;
import com.agileapes.couteau.reflection.error.PropertyTypeMismatchException;
import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.dragonfly.cg.SecuredInterfaceInterceptor;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessObject;
import com.agileapes.dragonfly.entity.InitializedEntity;
import com.agileapes.dragonfly.error.EntityDefinitionError;
import com.agileapes.dragonfly.error.EntityDeletedError;
import com.agileapes.dragonfly.error.NoPrimaryKeyDefinedError;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.impl.PrimaryKeyConstraintMetadata;
import com.agileapes.dragonfly.security.DataSecurityManager;
import com.agileapes.dragonfly.tools.ColumnNameFilter;
import com.agileapes.dragonfly.tools.ColumnPropertyFilter;

import java.io.Serializable;
import java.util.*;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 15:36)
 */
public class EntityProxy<E> extends SecuredInterfaceInterceptor implements InitializedEntity<E>, DataAccessObject<E, Serializable> {

    private final DataAccess dataAccess;
    private final TableMetadata<E> tableMetadata;
    private Class<E> entityType;
    private E entity;
    private E original;
    private String token;
    private final String keyProperty;
    private boolean deleted = false;
    private final Set<String> dirtiedProperties = new HashSet<String>();
    private final boolean isKeyGenerated;

    public EntityProxy(DataAccess dataAccess, TableMetadata<E> tableMetadata, DataSecurityManager securityManager) {
        super(securityManager);
        this.dataAccess = dataAccess;
        this.tableMetadata = tableMetadata;
        final Collection<PrimaryKeyConstraintMetadata> constraints = tableMetadata.getConstraints(PrimaryKeyConstraintMetadata.class);
        if (constraints.isEmpty() || constraints.iterator().next().getColumns().size() != 1) {
            keyProperty = null;
            isKeyGenerated = false;
        } else {
            keyProperty = constraints.iterator().next().getColumns().iterator().next().getPropertyName();
            isKeyGenerated = with(tableMetadata.getColumns()).keep(new ColumnNameFilter(keyProperty)).first().getGenerationType() != null;
        }
    }

    @Override
    public Object intercept(MethodDescriptor methodDescriptor, Object target, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        if (deleted) {
            throw new EntityDeletedError();
        }
        return super.intercept(methodDescriptor, target, arguments, methodProxy);
    }

    @Override
    public void initialize(Class<E> entityType, E entity, String key) {
        this.entityType = entityType;
        this.entity = entity;
        this.token = key;
    }

    @Override
    public void refresh() {
        final List<E> list = new ArrayList<E>();
        if (!hasKey() || accessKey() == null) {
            list.addAll(dataAccess.find(original));
        } else {
            list.add(dataAccess.find(tableMetadata.getEntityType(), accessKey()));
        }
        final E found = list.size() == 1 ? list.get(0) : null;
        if (found == null) {
            throw new NoPrimaryKeyDefinedError(tableMetadata);
        }
        final MethodBeanAccessor<?> accessor = new MethodBeanAccessor<Object>(found);
        final BeanWrapper<E> wrapper = new MethodBeanWrapper<E>(entity);
        //noinspection unchecked
        with(accessor.getPropertyNames()).keep(new Filter<String>() {
            @Override
            public boolean accepts(String propertyName) {
                try {
                    return wrapper.isWritable(propertyName);
                } catch (NoSuchPropertyException e) {
                    return false;
                }
            }
        }).each(new Processor<String>() {
            @Override
            public void process(String propertyName) {
                try {
                    wrapper.setPropertyValue(propertyName, accessor.getPropertyValue(propertyName));
                } catch (NoSuchPropertyException ignored) {
                } catch (PropertyAccessException e) {
                    throw new EntityDefinitionError("An entity definition error prevented transmutation of values", e);
                } catch (PropertyTypeMismatchException e) {
                    throw new EntityDefinitionError("An entity definition error prevented transmutation of values", e);
                }
            }
        });
        original = found;
        dirtiedProperties.clear();
        if (keyProperty != null) {
            dirtiedProperties.add(keyProperty);
        }
    }

    @Override
    public void save() {
        dataAccess.save(entity);
    }

    @Override
    public void delete() {
        dataAccess.delete(entityType, accessKey());
        deleted = true;
    }

    @Override
    public List<E> findLike() {
        return dataAccess.find(entity);
    }

    @Override
    public List<E> query(String queryName) {
        return dataAccess.executeQuery(entity, queryName);
    }

    @Override
    public int update(String queryName) {
        return dataAccess.executeUpdate(entity, queryName);
    }

    @Override
    public Serializable accessKey() {
        if (!hasKey()) {
            throw new NoPrimaryKeyDefinedError(tableMetadata);
        }
        if (!dirtiedProperties.contains(keyProperty)) {
            return null;
        }
        try {
            return new MethodBeanAccessor<E>(entity).getPropertyValue(keyProperty, Serializable.class);
        } catch (NoSuchPropertyException ignored) {
            return null;
        } catch (PropertyAccessException e) {
            throw new EntityDefinitionError("A definition error has caused the key property to be inaccessible", e);
        }
    }

    @Override
    public void changeKey(Serializable key) {
        if (!hasKey()) {
            throw new NoPrimaryKeyDefinedError(tableMetadata);
        }
        try {
            new MethodBeanWrapper<E>(entity).setPropertyValue(keyProperty, key);
        } catch (NoSuchPropertyException e) {
            throw new EntityDefinitionError("A definition error has caused the key property to be inaccessible", e);
        } catch (PropertyTypeMismatchException e) {
            throw new EntityDefinitionError("A definition error has caused the key property to be inaccessible", e);
        } catch (PropertyAccessException e) {
            throw new EntityDefinitionError("A definition error has caused the key property to be inaccessible", e);
        }
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setOriginalCopy(E originalCopy) {
        original = originalCopy;
    }

    @Override
    public E getOriginalCopy() {
        return original;
    }

    @Override
    public boolean isDirtied() {
        return !dirtiedProperties.isEmpty();
    }

    @Override
    public boolean hasKey() {
        return keyProperty != null;
    }

    @Override
    public String getQualifiedName() {
        return tableMetadata.getEntityType().getCanonicalName();
    }

    @Override
    public TableMetadata<E> getTableMetadata() {
        return tableMetadata;
    }

    @Override
    public boolean isKeyAutoGenerated() {
        return isKeyGenerated;
    }

    @Override
    protected Object call(MethodDescriptor methodDescriptor, Object target, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        if (methodDescriptor.getName().matches("set[A-Z].*")) {
            final String propertyName = ReflectionUtils.getPropertyName(methodDescriptor.getName());
            if (!with(tableMetadata.getColumns()).keep(new ColumnPropertyFilter(propertyName)).isEmpty()) {
                dirtiedProperties.add(propertyName);
            }
        }
        return methodProxy.callSuper(target, arguments);
    }

}
