package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.reflection.beans.BeanWrapper;
import com.agileapes.couteau.reflection.beans.impl.MethodBeanAccessor;
import com.agileapes.couteau.reflection.beans.impl.MethodBeanWrapper;
import com.agileapes.couteau.reflection.error.NoSuchPropertyException;
import com.agileapes.couteau.reflection.error.PropertyAccessException;
import com.agileapes.couteau.reflection.error.PropertyTypeMismatchException;
import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.dragonfly.api.DataAccess;
import com.agileapes.dragonfly.api.DataAccessObject;
import com.agileapes.dragonfly.entity.InitializedEntity;
import com.agileapes.dragonfly.error.EntityDefinitionError;
import com.agileapes.dragonfly.error.EntityDeletedError;
import com.agileapes.dragonfly.error.NoPrimaryKeyDefinedError;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.impl.PrimaryKeyConstraintMetadata;
import com.agileapes.dragonfly.tools.ColumnPropertyFilter;
import net.sf.cglib.proxy.MethodProxy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 15:36)
 */
public class EntityProxy<E> extends InterfaceInterceptor implements InitializedEntity<E>, DataAccessObject<E, Serializable> {

    private final DataAccess dataAccess;
    private final TableMetadata<E> tableMetadata;
    private Class<E> entityType;
    private E entity;
    private E original;
    private String token;
    private final String keyProperty;
    private boolean deleted = false;
    private final Set<String> dirtiedProperties = new HashSet<String>();

    public EntityProxy(DataAccess dataAccess, TableMetadata<E> tableMetadata) {
        this.dataAccess = dataAccess;
        this.tableMetadata = tableMetadata;
        final Collection<PrimaryKeyConstraintMetadata> constraints = tableMetadata.getConstraints(PrimaryKeyConstraintMetadata.class);
        if (constraints.isEmpty() || constraints.iterator().next().getColumns().size() != 1) {
            keyProperty = null;
        } else {
            keyProperty = constraints.iterator().next().getColumns().iterator().next().getPropertyName();
        }
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (deleted) {
            throw new EntityDeletedError();
        }
        return super.intercept(obj, method, args, proxy);
    }

    @Override
    protected Object call(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.getName().matches("set[A-Z].*")) {
            final String propertyName = ReflectionUtils.getPropertyName(method.getName());
            if (!with(tableMetadata.getColumns()).keep(new ColumnPropertyFilter(propertyName)).isEmpty()) {
                dirtiedProperties.add(propertyName);
            }
        }
        return proxy.invokeSuper(obj, args);
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

}
