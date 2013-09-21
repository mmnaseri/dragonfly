package com.agileapes.dragonfly.statement.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.couteau.context.error.RegistryException;
import com.agileapes.dragonfly.statement.Statement;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
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
    public Statement get(String name) throws RegistryException {
        return parent.get(getName(name));
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
                            localBeans.put(input, parent.get(input));
                        } catch (RegistryException e) {
                            throw new Error(e);
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
        return parent.get(getName(name), type);
    }

}
