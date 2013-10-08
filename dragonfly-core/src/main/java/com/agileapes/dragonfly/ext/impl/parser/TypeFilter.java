package com.agileapes.dragonfly.ext.impl.parser;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.basics.api.impl.NullFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/7, 23:23)
 */
public class TypeFilter implements Filter<Class<?>> {

    private final Pattern descriptor;
    private final boolean defaultPackage;
    private final TypeSelector selector;
    private final Set<Class<?>> checked = new HashSet<Class<?>>();
    private final String originalDescriptor;

    public TypeFilter(List<String> parts, TypeSelector selector) {
        this(with(parts).transform(new Transformer<String, String>() {
            @Override
            public String map(String input) {
                if (input.equals("*")) {
                    return "[^.]+";
                } else if (input.equals("..")) {
                    return "\\.([^.]+\\.)*";
                } else if (input.equals(".")) {
                    return "\\.";
                }
                return input;
            }
        }).join(""), selector);
    }

    public TypeFilter(String descriptor, TypeSelector selector) {
        this.descriptor = Pattern.compile(descriptor, Pattern.DOTALL);
        this.selector = selector;
        this.defaultPackage = !descriptor.contains(".");
        this.originalDescriptor = descriptor;
    }

    @Override
    public boolean accepts(Class<?> item) {
        if (checked.contains(item)) {
            return false;
        }
        if (descriptor.matcher(item.getCanonicalName()).matches()) {
            return true;
        }
        if (defaultPackage && item.getCanonicalName().matches("java\\.lang\\." + originalDescriptor)) {
            return true;
        }
        checked.add(item);
        if (TypeSelector.ASSIGNABLE.equals(selector)) {
            //noinspection unchecked
            final List<Class<?>> superTypes = with(item.getInterfaces()).add(item.getSuperclass()).drop(new NullFilter<Class<?>>()).list();
            for (Class<?> superType : superTypes) {
                if (accepts(superType)) {
                    return true;
                }
            }
        }
        return false;
    }
}
