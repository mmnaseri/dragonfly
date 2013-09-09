package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.cg.StaticNamingPolicy;
import com.agileapes.dragonfly.entity.InitializedEntity;
import com.agileapes.dragonfly.entity.ModifiableEntityContext;
import com.agileapes.dragonfly.metadata.TableMetadata;
import net.sf.cglib.proxy.Enhancer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 15:24)
 */
public class DefaultEntityContext implements ModifiableEntityContext {

    private final String key;
    private final DataAccess dataAccess;
    private final Map<Class<?>, Class<?>> interfaces = new HashMap<Class<?>, Class<?>>();

    public DefaultEntityContext(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.key = UUID.randomUUID().toString();
        for (Class<?> aClass : EntityProxy.class.getInterfaces()) {
            interfaces.put(aClass, null);
        }
    }
    
    @Override
    public <I> void addInterface(Class<I> ifc, Class<? extends I> implementation) {
        this.interfaces.put(ifc, implementation);
    }

    @Override
    public <E> E getInstance(Class<E> entityType) {
        throw new UnsupportedOperationException("This context cannot generate instances " +
                "without direct access to table metadata");
    }

    @Override
    public <E> E getInstance(TableMetadata<E> tableMetadata) {
        final EntityProxy<E> callback = new EntityProxy<E>(dataAccess, tableMetadata);
        callback.addInterfaces(interfaces);
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(tableMetadata.getEntityType());
        enhancer.setInterfaces(interfaces.keySet().toArray(new Class[interfaces.size()]));
        enhancer.setCallback(callback);
        enhancer.setNamingPolicy(new StaticNamingPolicy("entity"));
        final E proxy = tableMetadata.getEntityType().cast(enhancer.create());
        //noinspection unchecked
        ((InitializedEntity<E>) proxy).initialize(tableMetadata.getEntityType(), proxy, key);
        //noinspection unchecked
        ((InitializedEntity<E>) proxy).setOriginalCopy(proxy);
        return proxy;
    }

    @Override
    public <E> boolean has(E entity) {
        return entity instanceof InitializedEntity && ((InitializedEntity) entity).getToken().equals(key);
    }

}
