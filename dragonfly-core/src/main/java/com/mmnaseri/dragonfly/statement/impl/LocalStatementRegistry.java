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

package com.mmnaseri.dragonfly.statement.impl;

import com.mmnaseri.couteau.basics.api.Filter;
import com.mmnaseri.couteau.basics.api.Processor;
import com.mmnaseri.couteau.context.error.InvalidBeanTypeException;
import com.mmnaseri.couteau.context.error.NoSuchItemException;
import com.mmnaseri.couteau.context.error.RegistryException;
import com.mmnaseri.dragonfly.statement.Statement;
import com.mmnaseri.dragonfly.statement.StatementRegistry;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * This is a statement registry that reflects only a part of a given statement registry, limited to a
 * prefix.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/21, 13:49)
 */
public class LocalStatementRegistry implements StatementRegistry {

    private final StatementRegistry parent;
    private final String prefix;
    private final Map<String, Statement> localBeans = new ConcurrentHashMap<String, Statement>();

    public LocalStatementRegistry(StatementRegistry parent, String prefix) {
        this.parent = parent;
        this.prefix = prefix.endsWith(".") ? prefix : prefix + ".";
        updateLocalBeans();
    }

    private String getName(String name) {
        return prefix + name;
    }

    @Override
    public Class<Statement> getRegistryType() {
        return Statement.class;
    }

    @Override
    public boolean contains(String name) {
        return parent.contains(getName(name));
    }

    @Override
    public void register(String name, Statement item) throws RegistryException {
        parent.register(getName(name), item);
        updateLocalBeans();
    }

    @Override
    public void unregister(String name) throws RegistryException {
        parent.unregister(getName(name));
    }

    @Override
    public void replace(String name, Statement item) throws RegistryException {
        parent.replace(name, item);
    }

    @Override
    public Statement get(String name) throws RegistryException {
        if (!localBeans.containsKey(name)) {
            throw new NoSuchItemException(name);
        }
        return localBeans.get(name);
    }

    @Override
    public Collection<Statement> getBeans() {
        return localBeans.values();
    }

    private synchronized void updateLocalBeans() {
        //noinspection unchecked
        with(parent.getBeanNames())
                .keep(new Filter<String>() {
                    @Override
                    public boolean accepts(String item) {
                        return item.startsWith(prefix);
                    }
                })
                .each(new Processor<String>() {
                    @Override
                    public void process(String input) {
                        try {
                            localBeans.put(input.substring(prefix.length()), parent.get(input));
                        } catch (RegistryException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    @Override
    public Collection<String> getBeanNames() {
        return localBeans.keySet();
    }

    @Override
    public <T extends Statement> T get(String name, Class<T> type) throws RegistryException {
        if (!localBeans.containsKey(name)) {
            throw new NoSuchItemException(name);
        }
        final Statement value = localBeans.get(name);
        if (value == null || type.isInstance(value)) {
            return type.cast(value);
        }
        throw new InvalidBeanTypeException(name, type, value.getClass());
    }

}
