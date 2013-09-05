package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.entity.InitializedEntity;
import com.agileapes.dragonfly.metadata.TableMetadata;
import net.sf.cglib.proxy.Enhancer;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 15:24)
 */
public class DefaultEntityContext implements EntityContext {

    @Override
    public <E> E getInstance(TableMetadata<E> tableMetadata) {
        final E proxy = tableMetadata.getEntityType().cast(
                Enhancer.create(
                        tableMetadata.getEntityType(),
                        EntityProxy.class.getInterfaces(),
                        new EntityProxy<E>(tableMetadata)
                )
        );
        //noinspection unchecked
        ((InitializedEntity<E>) proxy).initialize(proxy);
        return proxy;
    }

}
