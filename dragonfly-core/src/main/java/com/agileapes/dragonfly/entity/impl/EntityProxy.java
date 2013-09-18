package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.enhancer.api.MethodDescriptor;
import com.agileapes.couteau.enhancer.api.MethodProxy;
import com.agileapes.couteau.reflection.beans.BeanWrapper;
import com.agileapes.couteau.reflection.beans.impl.MethodBeanWrapper;
import com.agileapes.couteau.reflection.error.NoSuchPropertyException;
import com.agileapes.couteau.reflection.error.PropertyAccessException;
import com.agileapes.couteau.reflection.error.PropertyTypeMismatchException;
import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.couteau.reflection.util.assets.GetterMethodFilter;
import com.agileapes.dragonfly.cg.SecuredInterfaceInterceptor;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessObject;
import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.entity.EntityHandler;
import com.agileapes.dragonfly.entity.InitializedEntity;
import com.agileapes.dragonfly.error.EntityDeletedError;
import com.agileapes.dragonfly.error.NoPrimaryKeyDefinedError;
import com.agileapes.dragonfly.metadata.ReferenceMetadata;
import com.agileapes.dragonfly.metadata.RelationType;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.security.DataSecurityManager;
import com.agileapes.dragonfly.tools.ColumnPropertyFilter;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * This class is the default interceptor used throughout the application to intercept any given
 * entity's dynamic method introduction calls.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 15:36)
 */
public class EntityProxy<E> extends SecuredInterfaceInterceptor implements InitializedEntity<E>, DataAccessObject<E, Serializable> {

    private final DataAccess dataAccess;
    private final TableMetadata<E> tableMetadata;
    private final EntityHandler<E> handler;
    private final EntityContext entityContext;
    private Class<E> entityType;
    private E entity;
    private E original;
    private String token;
    private boolean deleted = false;
    private final Set<String> dirtiedProperties = new HashSet<String>();
    private boolean frozen = false;

    public EntityProxy(DataAccess dataAccess, TableMetadata<E> tableMetadata, DataSecurityManager securityManager, EntityHandler<E> handler, EntityContext entityContext) {
        super(securityManager);
        this.dataAccess = dataAccess;
        this.tableMetadata = tableMetadata;
        this.handler = handler;
        this.entityContext = entityContext;
    }

    @Override
    public Object intercept(MethodDescriptor methodDescriptor, Object target, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        if (deleted) {
            throw new EntityDeletedError();
        }
        return super.intercept(methodDescriptor, target, arguments, methodProxy);
    }

    @Override
    public void initialize(Class<E> entityType, E entity, String token) {
        this.entityType = entityType;
        this.entity = entity;
        this.token = token;
    }

    @Override
    public void refresh() {
        freeze();
        final List<E> list = new ArrayList<E>();
        if (!hasKey() || accessKey() == null) {
            list.addAll(dataAccess.find(original));
        } else {
            list.add(dataAccess.find(tableMetadata.getEntityType(), accessKey()));
        }
        final E found = list.size() == 1 ? list.get(0) : null;
        if (found == null) {
            throw new NoPrimaryKeyDefinedError(entityType);
        }
        ((InitializedEntity) found).freeze();
        handler.copy(found, entity);
        ((InitializedEntity) found).unfreeze();
        original = found;
        dirtiedProperties.clear();
        if (hasKey()) {
            dirtiedProperties.add(handler.getKeyProperty());
        }
        unfreeze();
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
        return handler.getKey(entity);
    }

    @Override
    public void changeKey(Serializable key) {
        handler.setKey(entity, key);
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
    public synchronized void freeze() {
        frozen = true;
    }

    @Override
    public synchronized void unfreeze() {
        frozen = false;
    }

    @Override
    public boolean hasKey() {
        return handler.hasKey();
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
        return handler.isKeyAutoGenerated();
    }

    @Override
    public void loadRelations() {
        final BeanWrapper<E> wrapper = new MethodBeanWrapper<E>(entity);
        //noinspection unchecked
        with(tableMetadata.getForeignReferences())
        .keep(new Filter<ReferenceMetadata<E, ?>>() {
            @Override
            public boolean accepts(ReferenceMetadata<E, ?> item) {
                return !item.isLazy();
            }
        }).each(new Processor<ReferenceMetadata<E, ?>>() {
            @Override
            public void process(ReferenceMetadata<E, ?> referenceMetadata) {
                if (referenceMetadata.getRelationType().equals(RelationType.ONE_TO_MANY)) {
                    try {
                        wrapper.setPropertyValue(referenceMetadata.getPropertyName(), loadOneToMany(referenceMetadata));
                    } catch (NoSuchPropertyException ignored) {
                    } catch (PropertyAccessException ignored) {
                    } catch (PropertyTypeMismatchException e) {
                        throw new Error("Invalid property type", e);
                    }
                }
            }
        });
    }

    @Override
    public void saveRelations() {
        handler.prepareRelations(entity);
        Collection<?> relatedItems = handler.getRelatedItems(entity);
        for (Object item : relatedItems) {
            dataAccess.save(item);
        }
    }

    @Override
    public void deleteRelations() {
        handler.prepareRelations(entity);
        Collection<?> relatedItems = handler.getRelatedItems(entity);
        for (Object item : relatedItems) {
            dataAccess.delete(item);
        }
    }

    @Override
    protected Object call(MethodDescriptor methodDescriptor, Object target, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        if (methodDescriptor.getName().matches("set[A-Z].*")) {
            final String propertyName = ReflectionUtils.getPropertyName(methodDescriptor.getName());
            if (!with(tableMetadata.getColumns()).keep(new ColumnPropertyFilter(propertyName)).isEmpty()) {
                dirtiedProperties.add(propertyName);
            }
        }
        final Method method = methodDescriptor.getMethod();
        if (new GetterMethodFilter().accepts(method)) {
            final String propertyName = ReflectionUtils.getPropertyName(method.getName());
            //noinspection unchecked
            final ReferenceMetadata<E, ?> referenceMetadata = with(tableMetadata.getForeignReferences()).keep(new Filter<ReferenceMetadata<E, ?>>() {
                @Override
                public boolean accepts(ReferenceMetadata<E, ?> item) {
                    return propertyName.equals(item.getPropertyName());
                }
            }).first();
            if (!frozen) {
                if (!dirtiedProperties.contains(propertyName) && referenceMetadata != null && referenceMetadata.getRelationType().equals(RelationType.ONE_TO_MANY) && referenceMetadata.isLazy()) {
                    final BeanWrapper<E> wrapper = new MethodBeanWrapper<E>(entity);
                    wrapper.setPropertyValue(propertyName, loadOneToMany(referenceMetadata));
                    dirtiedProperties.add(propertyName);
                }
            }
        }
        return methodProxy.callSuper(target, arguments);
    }

    private Collection<?> loadOneToMany(ReferenceMetadata<E, ?> referenceMetadata) {
        final Object foreignEntity = entityContext.getInstance(referenceMetadata.getForeignTable().getEntityType());
        final BeanWrapper<?> wrapper = new MethodBeanWrapper<Object>(foreignEntity);
        try {
            wrapper.setPropertyValue(referenceMetadata.getForeignColumn().getPropertyName(), entity);
        } catch (Exception e) {
            throw new Error(e);
        }
        return ((DataAccessObject<?, ?>) foreignEntity).findLike();
    }

}
