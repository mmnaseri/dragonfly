package com.agileapes.dragonfly.entity.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.basics.api.impl.NullFilter;
import com.agileapes.couteau.reflection.util.assets.MemberNameFilter;
import com.agileapes.couteau.reflection.util.assets.MethodArgumentsFilter;
import com.agileapes.couteau.reflection.util.assets.MethodReturnTypeFilter;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;
import static com.agileapes.couteau.reflection.util.ReflectionUtils.withMethods;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 15:49)
 */
public abstract class InterfaceInterceptor implements MethodInterceptor {

    protected abstract Object call(Object obj, Method method, Object[] args, MethodProxy proxy) throws  Throwable;

    @Override
    @SuppressWarnings("unchecked")
    public Object intercept(Object obj, final Method method, final Object[] args, MethodProxy proxy) throws Throwable {
        final Method targetMethod = with(getClass().getInterfaces()).transform(new Transformer<Class<?>, Method>() {
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
            final Method currentMethod = withMethods(getClass())
                    .keep(new MemberNameFilter(targetMethod.getName()))
                    .keep(new MethodReturnTypeFilter(targetMethod.getReturnType()))
                    .keep(new MethodArgumentsFilter(targetMethod.getParameterTypes()))
                    .keep(new Filter<Method>() {
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
                    })
                    .first();
            if (currentMethod != null) {
                return currentMethod.invoke(this, args);
            }
        }
        return call(obj, method, args, proxy);
    }

}
