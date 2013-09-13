package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.basics.api.Cache;
import com.agileapes.couteau.basics.api.impl.ConcurrentCache;
import com.agileapes.couteau.enhancer.api.ClassEnhancer;
import com.agileapes.couteau.enhancer.api.Interceptible;
import com.agileapes.couteau.enhancer.impl.GeneratingClassEnhancer;
import com.agileapes.dragonfly.cg.StaticNamingPolicy;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.entity.InitializedEntity;
import com.agileapes.dragonfly.entity.ModifiableEntityContext;
import com.agileapes.dragonfly.error.EntityInitializationError;
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
    private final DataAccess dataAccess;
    private final Map<Class<?>, Class<?>> interfaces = new HashMap<Class<?>, Class<?>>();
    private final DataSecurityManager securityManager;
    private final Cache<Class<?>, Class<?>> cache = new ConcurrentCache<Class<?>, Class<?>>();

    public DefaultEntityContext(DataAccess dataAccess, DataSecurityManager securityManager) {
        this.dataAccess = dataAccess;
        this.securityManager = securityManager;
        this.key = UUID.randomUUID().toString();
    }
    
    @Override
    public <I> void addInterface(Class<I> ifc, Class<? extends I> implementation) {
        cache.invalidate();
        this.interfaces.put(ifc, implementation);
    }

    @Override
    public <E> E getInstance(Class<E> entityType) {
        throw new UnsupportedOperationException("This context cannot generate instances " +
                "without direct access to table metadata");
    }

    @Override
    public <E> E getInstance(TableMetadata<E> tableMetadata) {
        final EntityProxy<E> entityProxy = new EntityProxy<E>(dataAccess, tableMetadata, securityManager);
        for (Map.Entry<Class<?>, Class<?>> entry : interfaces.entrySet()) {
            entityProxy.addInterface(entry.getKey(), entry.getValue());
        }
        final E proxy = enhanceObject(tableMetadata.getEntityType(), entityProxy);
        //noinspection unchecked
        ((InitializedEntity<E>) proxy).initialize(tableMetadata.getEntityType(), proxy, key);
        //noinspection unchecked
        ((InitializedEntity<E>) proxy).setOriginalCopy(proxy);
        return proxy;
    }

    private <E> E enhanceObject(Class<E> type, EntityProxy<E> entityProxy) {
        final Class<? extends E> enhancedClass = enhanceClass(type, entityProxy);
        final E entity;
        try {
            entity = enhancedClass.newInstance();
        } catch (Exception e) {
            throw new EntityInitializationError(type, e);
        }
        ((Interceptible) entity).setInterceptor(entityProxy);
        return entity;
    }

    private <E> Class<? extends E> enhanceClass(Class<E> original, EntityProxy<E> entityProxy) {
        if (cache.contains(original)) {
            //noinspection unchecked
            return (Class<? extends E>) cache.read(original);
        }
        final ClassEnhancer<E> classEnhancer = new GeneratingClassEnhancer<E>();
        classEnhancer.setInterfaces(entityProxy.getInterfaces());
        classEnhancer.setSuperClass(original);
        classEnhancer.setNamingPolicy(new StaticNamingPolicy("entity"));
        final Class<? extends E> enhanced = classEnhancer.enhance();
        cache.write(original, enhanced);
        return enhanced;
    }

    @Override
    public <E> boolean has(E entity) {
        return entity instanceof InitializedEntity && ((InitializedEntity) entity).getToken().equals(key);
    }

}
