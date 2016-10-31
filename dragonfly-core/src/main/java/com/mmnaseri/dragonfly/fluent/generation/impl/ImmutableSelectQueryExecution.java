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

package com.mmnaseri.dragonfly.fluent.generation.impl;

import com.mmnaseri.dragonfly.fluent.SelectQueryExecution;
import com.mmnaseri.dragonfly.fluent.generation.JoinedSelectionSource;
import com.mmnaseri.dragonfly.fluent.generation.ParameterDescriptor;
import com.mmnaseri.dragonfly.fluent.generation.SelectionSource;
import com.mmnaseri.dragonfly.metadata.ColumnMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/10 AD, 11:00)
 */
public class ImmutableSelectQueryExecution<E, H> implements SelectQueryExecution<E, H> {

    private final Class<? extends H> bindingType;
    private final H binding;
    private final String sql;
    private final List<ParameterDescriptor> parameters;
    private final SelectionSource<E> source;
    private final List<JoinedSelectionSource<?, ?>> joinedSources;
    private final Map<Object, String> columnAliases;
    private final Map<Object, String> tableAliases;
    private final Map<Object, ColumnMetadata> columns;
    private final List<SelectionSource<?>> sources;

    public ImmutableSelectQueryExecution(Class<? extends H> bindingType, H binding, String sql, List<ParameterDescriptor> parameters, SelectionSource<E> source, List<JoinedSelectionSource<?, ?>> joinedSources, Map<Object, String> tableAliases, Map<Object, String> columnAliases, Map<Object, ColumnMetadata> columns) {
        this.bindingType = bindingType;
        this.binding = binding;
        this.sql = sql;
        this.parameters = parameters;
        this.source = source;
        this.joinedSources = joinedSources;
        this.columnAliases = columnAliases;
        this.tableAliases = tableAliases;
        this.columns = columns;
        this.sources = new ArrayList<SelectionSource<?>>();
        sources.add(source);
        sources.addAll(joinedSources);
    }

    @Override
    public Class<? extends H> getBindingType() {
        return bindingType;
    }

    @Override
    public H getBinding() {
        return binding;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public List<ParameterDescriptor> getParameters() {
        return parameters;
    }

    @Override
    public SelectionSource<E> getMainSource() {
        return source;
    }

    @Override
    public List<JoinedSelectionSource<?, ?>> getJoinedSources() {
        return joinedSources;
    }

    @Override
    public List<SelectionSource<?>> getSources() {
        return sources;
    }

    @Override
    public Map<Object, String> getColumnAliases() {
        return columnAliases;
    }

    @Override
    public Map<Object, String> getTableAliases() {
        return tableAliases;
    }

    @Override
    public Map<Object, ColumnMetadata> getColumns() {
        return columns;
    }

}
