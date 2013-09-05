package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.dragonfly.entity.InitializedEntity;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.tools.ColumnPropertyFilter;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 15:36)
 */
public class EntityProxy<E> extends InterfaceInterceptor implements InitializedEntity<E> {

    private final TableMetadata<E> tableMetadata;
    private E entity;

    public EntityProxy(TableMetadata<E> tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    @Override
    protected Object call(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.getName().matches("set[A-Z].*")) {
            final String propertyName = ReflectionUtils.getPropertyName(method.getName());
            if (!with(tableMetadata.getColumns()).keep(new ColumnPropertyFilter(propertyName)).isEmpty()) {
                System.out.print("");
            }
        }
        return proxy.invokeSuper(obj, args);
    }

    @Override
    public void initialize(E entity) {
        this.entity = entity;
    }

}
