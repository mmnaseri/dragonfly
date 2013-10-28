package com.agileapes.dragonfly.ext.impl.parser;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.reflection.util.ReflectionUtils;
import com.agileapes.couteau.reflection.util.assets.GetterMethodFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import static com.agileapes.couteau.reflection.util.ReflectionUtils.withMethods;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/8, 16:43)
 */
public class HavingPropertyFilter implements Filter<Class<?>> {

    private final List<Filter<Class<?>>> annotations;
    private final Filter<Class<?>> typeFilter;
    private final String propertyName;

    public HavingPropertyFilter(List<Filter<Class<?>>> annotations, Filter<Class<?>> typeFilter, String propertyName) {
        this.annotations = annotations;
        this.typeFilter = typeFilter;
        this.propertyName = propertyName;
    }

    @Override
    public boolean accepts(Class<?> item) {
        //noinspection unchecked
        return !withMethods(item)
                .keep(new GetterMethodFilter())
                .keep(new Filter<Method>() {
                    @Override
                    public boolean accepts(Method item) {
                        final String accessorProperty = ReflectionUtils.getPropertyName(item.getName());
                        return accessorProperty.matches(propertyName);
                    }
                })
                .keep(new Filter<Method>() {
                    @Override
                    public boolean accepts(Method item) {
                        return typeFilter.accepts(item.getReturnType());
                    }
                })
                .keep(new Filter<Method>() {
                    @Override
                    public boolean accepts(Method item) {
                        for (Filter<Class<?>> annotationFilter : annotations) {
                            boolean found = false;
                            for (Annotation annotation : item.getAnnotations()) {
                                if (annotationFilter.accepts(annotation.annotationType())) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                return false;
                            }
                        }
                        return true;
                    }
                })
                .isEmpty();
    }

}
