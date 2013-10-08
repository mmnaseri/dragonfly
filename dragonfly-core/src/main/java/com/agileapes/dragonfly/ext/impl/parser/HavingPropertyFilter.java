package com.agileapes.dragonfly.ext.impl.parser;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.reflection.util.assets.GetterMethodFilter;
import com.agileapes.couteau.reflection.util.assets.PropertyAccessorFilter;

import java.lang.reflect.Method;
import java.util.List;

import static com.agileapes.couteau.reflection.util.ReflectionUtils.withMethods;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/8, 16:43)
 */
public class HavingPropertyFilter implements Filter<Class<?>> {

    private final Filter<Class<?>> typeFilter;
    private final String propertyName;

    public HavingPropertyFilter(Filter<Class<?>> typeFilter, String propertyName) {
        this.typeFilter = typeFilter;
        this.propertyName = propertyName;
    }

    public HavingPropertyFilter(List<String> propertyType, TypeSelector typeSelector, String propertyName) {
        this(new TypeFilter(propertyType, typeSelector), propertyName);
    }

    @Override
    public boolean accepts(Class<?> item) {
        final Method method = withMethods(item).keep(new GetterMethodFilter()).find(new PropertyAccessorFilter(propertyName));
        return method != null && typeFilter.accepts(method.getReturnType());
    }

}
