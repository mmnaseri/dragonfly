package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.basics.api.Cache;
import com.agileapes.couteau.basics.api.impl.ConcurrentCache;
import com.agileapes.couteau.enhancer.api.ClassEnhancer;
import com.agileapes.couteau.enhancer.impl.GeneratingClassEnhancer;
import com.agileapes.couteau.reflection.util.ClassUtils;
import com.agileapes.dragonfly.cg.StaticNamingPolicy;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.entity.*;
import com.agileapes.dragonfly.metadata.MetadataRegistry;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.security.DataSecurityManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This is a caching entity context that will use a generating class enhancer for enhancement
 * purposes.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 15:24)
 */
public class DefaultEntityContext implements ModifiableEntityContext {

    private final String key;
    private DataAccess dataAccess;
    private EntityHandlerContext handlerContext;
    private final Map<Class<?>, Map<Class<?>, Class<?>>> interfaces = new HashMap<Class<?>, Map<Class<?>, Class<?>>>();
    private final DataSecurityManager securityManager;
    private final MetadataRegistry metadataRegistry;
    private final Cache<Class<?>, EntityFactory<?>> cache = new ConcurrentCache<Class<?>, EntityFactory<?>>();
    private final DataAccessSession session;

    public DefaultEntityContext(DataSecurityManager securityManager, MetadataRegistry metadataRegistry, DataAccessSession session) {
        this.securityManager = securityManager;
        this.metadataRegistry = metadataRegistry;
        this.session = session;
        this.key = UUID.randomUUID().toString();
    }

    @Override
    public <E> E getInstance(Class<E> entityType) {
        return getInstance(metadataRegistry.getTableMetadata(entityType));
    }

    @Override
    public <E> E getInstance(TableMetadata<E> tableMetadata) {
        final EntityHandler<E> entityHandler = handlerContext.getHandler(tableMetadata.getEntityType());
        final EntityProxy<E> entityProxy = new EntityProxy<E>(securityManager, tableMetadata, entityHandler, dataAccess, session, this);
        if (interfaces.containsKey(tableMetadata.getEntityType())) {
            final Map<Class<?>, Class<?>> classMap = interfaces.get(tableMetadata.getEntityType());
            for (Map.Entry<Class<?>, Class<?>> entry : classMap.entrySet()) {
                entityProxy.addInterface(entry.getKey(), entry.getValue());
            }
        }
        final E entity = enhanceObject(tableMetadata.getEntityType(), entityProxy);
        if (entity instanceof InitializedEntity<?>) {
            //noinspection unchecked
            InitializedEntity<E> initializedEntity = (InitializedEntity<E>) entity;
            initializedEntity.initialize(tableMetadata.getEntityType(), entity, key);
        }
        return entity;
    }

    private synchronized <E> E enhanceObject(Class<E> type, EntityProxy<E> entityProxy) {
        final EntityFactory<E> factory = getFactory(type, entityProxy);
        return factory.getInstance(entityProxy);
    }

    private <E> EntityFactory<E> getFactory(Class<E> entityType, EntityProxy<E> entityProxy) {
        if (cache.contains(entityType)) {
            //noinspection unchecked
            return (EntityFactory<E>) cache.read(entityType);
        }
        final Class<? extends E> enhancedType = enhanceClass(entityType, entityProxy);
//        noinspection unchecked
        final EntityFactoryBuilder<E> builder = new EntityFactoryBuilder<E>(entityType, enhancedType);
        final EntityFactory<E> entityFactory = builder.getEntityFactory();
        cache.write(entityType, entityFactory);
        return entityFactory;
    }

    private <E> Class<? extends E> enhanceClass(Class<E> original, EntityProxy<E> entityProxy) {
        final StaticNamingPolicy namingPolicy = new StaticNamingPolicy("Entity");
        final String className = namingPolicy.getClassName(original, null);
        Class<? extends E> enhancedClass = null;
        try {
            enhancedClass = ((Class<?>) ClassUtils.forName(className, original.getClassLoader())).asSubclass(original);
        } catch (ClassNotFoundException ignored) {
        }
        if (enhancedClass != null) {
            return enhancedClass;
        }
        final ClassEnhancer<E> classEnhancer = new GeneratingClassEnhancer<E>();
        classEnhancer.setInterfaces(entityProxy.getInterfaces());
        classEnhancer.setSuperClass(original);
        classEnhancer.setNamingPolicy(namingPolicy);
        return classEnhancer.enhance();
    }

    @Override
    public <E> boolean has(E entity) {
        if (entity instanceof InitializedEntity) {
            InitializedEntity initializedEntity = (InitializedEntity) entity;
            return key.equals(initializedEntity.getToken());
        }
        return false;
    }

    public void setDataAccess(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public void setInterfaces(Map<Class<?>, Map<Class<?>, Class<?>>> interfaces) {
        this.interfaces.clear();
        this.interfaces.putAll(interfaces);
    }

    @Override
    public void setEntityFactories(Map<Class<?>, EntityFactory<?>> factories) {
        for (Map.Entry<Class<?>, EntityFactory<?>> entry : factories.entrySet()) {
            cache.write(entry.getKey(), entry.getValue());
        }
    }

    public void setHandlerContext(EntityHandlerContext handlerContext) {
        this.handlerContext = handlerContext;
    }
}
