package com.agileapes.dragonfly.ext.impl.parser;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/7, 23:45)
 */
public class AnnotationTypeFilter implements Filter<Class<?>> {

    private final Set<Class<?>> checked = new HashSet<Class<?>>();
    private final TypeFilter filter;

    public AnnotationTypeFilter(List<String> parts) {
        this(with(parts).transform(new Transformer<String, String>() {
            @Override
            public String map(String input) {
                if (input.equals("*")) {
                    return "[^.]+";
                } else if (input.equals("..")) {
                    return "\\.([^.]+\\.)*";
                }
                return input;
            }
        }).join(""));
    }

    public AnnotationTypeFilter(String descriptor) {
        this.filter = new TypeFilter(descriptor, TypeSelector.EQUALS);
    }


    @Override
    public boolean accepts(Class<?> item) {
        for (Annotation annotation : item.getDeclaredAnnotations()) {
            if (filter.accepts(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

}
