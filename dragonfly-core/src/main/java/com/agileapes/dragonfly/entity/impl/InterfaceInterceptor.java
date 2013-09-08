package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.basics.api.impl.FilterChain;
import com.agileapes.couteau.basics.api.impl.NullFilter;
import com.agileapes.couteau.reflection.beans.BeanInitializer;
import com.agileapes.couteau.reflection.beans.impl.ConstructorBeanInitializer;
import com.agileapes.couteau.reflection.error.BeanInstantiationException;
import com.agileapes.couteau.reflection.util.assets.MemberNameFilter;
import com.agileapes.couteau.reflection.util.assets.MethodArgumentsFilter;
import com.agileapes.couteau.reflection.util.assets.MethodReturnTypeFilter;
import com.agileapes.dragonfly.error.EntityInitializationError;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;
import static com.agileapes.couteau.reflection.util.ReflectionUtils.withMethods;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 15:49)
 */
public abstract class InterfaceInterceptor implements MethodInterceptor {

    private final Map<Class<?>, Object> targets = new ConcurrentHashMap<Class<?>, Object>();
    private final BeanInitializer initializer = new ConstructorBeanInitializer();
    private final List<Class<?>> superTypes = new CopyOnWriteArrayList<Class<?>>();

    protected InterfaceInterceptor() {
        Collections.addAll(superTypes, getClass().getInterfaces());
    }

    protected abstract Object call(Object obj, Method method, Object[] args, MethodProxy proxy) throws  Throwable;

    @Override
    @SuppressWarnings("unchecked")
    public Object intercept(Object obj, final Method method, final Object[] args, MethodProxy proxy) throws Throwable {
        final Method targetMethod = with(superTypes).transform(new Transformer<Class<?>, Method>() {
            @Override
            public Method map(Class<?> superType) {
                return withMethods(superType).keep(new Filter<Method>() {
                    @Override
                    public boolean accepts(Method item) {
                        return item.equals(method);
                    }
                }).first();
            }
        }).drop(new NullFilter<Method>()).first();
        if (targetMethod != null) {
            final FilterChain<Method> filterChain = new FilterChain<Method>();
            filterChain.addFilter(new MemberNameFilter(targetMethod.getName()));
            filterChain.addFilter(new MethodReturnTypeFilter(targetMethod.getReturnType()));
            filterChain.addFilter(new MethodArgumentsFilter(targetMethod.getParameterTypes()));
            filterChain.addFilter(new Filter<Method>() {
                @Override
                public boolean accepts(Method method) {
                    final Class<?>[] parameterTypes = method.getParameterTypes();
                    for (int i = 0; i < parameterTypes.length; i++) {
                        final Class<?> type = parameterTypes[i];
                        if (!type.isInstance(args[i])) {
                            return false;
                        }
                    }
                    return true;
                }
            });
            Method currentMethod = withMethods(getClass()).keep(filterChain).first();
            if (currentMethod != null) {
                return currentMethod.invoke(this, args);
            } else {
                final Class<?> superType = targetMethod.getDeclaringClass();
                currentMethod = withMethods(superType).keep(filterChain).first();
                if (currentMethod != null) {
                    return currentMethod.invoke(targets.get(superType), args);
                }
            }
        }
        return call(obj, method, args, proxy);
    }

    public void addInterfaces(Map<Class<?>, Class<?>> interfaces) {
        for (Map.Entry<Class<?>, Class<?>> entry : interfaces.entrySet()) {
            if (entry.getValue() != null) {
                try {
                    targets.put(entry.getKey(), initializer.initialize(entry.getValue(), new Class[0]));
                    superTypes.add(entry.getKey());
                } catch (BeanInstantiationException e) {
                    throw new EntityInitializationError(entry.getKey(), e);
                }
            }
        }
    }

}
