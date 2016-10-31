/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mmnaseri.dragonfly.ext.impl.parser;

import com.mmnaseri.couteau.basics.api.Filter;
import com.mmnaseri.couteau.basics.api.Transformer;
import com.mmnaseri.couteau.basics.api.impl.NullFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
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
