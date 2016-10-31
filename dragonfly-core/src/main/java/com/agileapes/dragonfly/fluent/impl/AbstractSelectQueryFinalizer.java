/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
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

package com.agileapes.dragonfly.fluent.impl;

import com.mmnaseri.couteau.basics.api.Filter;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.entity.MapEntityCreator;
import com.agileapes.dragonfly.entity.impl.DefaultMapEntityCreator;
import com.agileapes.dragonfly.fluent.SelectQueryExecution;
import com.agileapes.dragonfly.fluent.SelectQueryFinalizer;
import com.agileapes.dragonfly.fluent.error.DatabaseNegotiationException;
import com.agileapes.dragonfly.fluent.error.StatementPreparationException;
import com.agileapes.dragonfly.fluent.generation.*;
import com.agileapes.dragonfly.fluent.generation.impl.ImmutableMapping;
import com.agileapes.dragonfly.fluent.generation.impl.ImmutableSelectionSource;
import com.agileapes.dragonfly.fluent.generation.impl.ResolvableFunctionInvocation;
import com.agileapes.dragonfly.fluent.generation.impl.SelectQueryExecutionBuilder;
import com.agileapes.dragonfly.fluent.tools.QueryBuilderTools;
import com.agileapes.dragonfly.fluent.tools.QueryResultBinder;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.statement.Statement;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/10 AD, 12:36)
 */
public abstract class AbstractSelectQueryFinalizer<E> implements SelectQueryFinalizer<E> {

    private final DataAccessSession session;
    private final SelectQueryExecutionBuilder<E> executionBuilder;

    protected AbstractSelectQueryFinalizer(DataAccessSession session, E entity) {
        this.session = session;
        final TableMetadata<E> tableMetadata = getTableMetadata(entity);
        this.executionBuilder = new SelectQueryExecutionBuilder<E>();
        executionBuilder.setMainSource(new ImmutableSelectionSource<E>(entity, tableMetadata));
    }

    protected <O> TableMetadata<O> getTableMetadata(O entity) {
        //noinspection unchecked
        return getTableMetadata((Class<O>) entity.getClass());
    }

    protected  <O> TableMetadata<O> getTableMetadata(Class<O> entityType) {
        return session.getTableMetadataRegistry().getTableMetadata(entityType);
    }

    private <H> SelectQueryExecution<E, H> selection(H binding, boolean distinct) {
        return executionBuilder.build(session, binding, distinct);
    }

    private <H> List<Map<Mapping, Object>> execute(SelectQueryExecution<E, H> selection) {
        final Connection connection = session.getConnection();
        final PreparedStatement preparedStatement;
        try {
            final String sql = selection.getSql() + ";";
            LogFactory.getLog(Statement.class).info("Preparing statement: " + sql);
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new DatabaseNegotiationException("Failed to get a prepared statement from the database", e);
        }
        for (ParameterDescriptor descriptor : selection.getParameters()) {
            try {
                if (descriptor.getValue() == null) {
                    preparedStatement.setNull(descriptor.getIndex(), descriptor.getSqlType());
                } else {
                    preparedStatement.setObject(descriptor.getIndex(), descriptor.getValue());
                }
            } catch (SQLException e) {
                throw new StatementPreparationException("Failed to prepare statement for parameter " + descriptor.getIndex(), e);
            }
        }
        final ResultSet resultSet;
        final ResultSetMetaData metaData;
        try {
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new DatabaseNegotiationException("Failed to retrieve the results from the data source", e);
        }
        try {
            metaData = resultSet.getMetaData();
        } catch (SQLException e) {
            throw new DatabaseNegotiationException("Failed to get result set metadata for query", e);
        }
        final ArrayList<Map<Mapping, Object>> result = new ArrayList<Map<Mapping, Object>>();
        while (true) {
            try {
                if (!resultSet.next()) {
                    break;
                }
                final HashMap<Mapping, Object> map = new HashMap<Mapping, Object>();
                for (int i = 1; i <= metaData.getColumnCount(); i ++) {
                    map.put(new ImmutableMapping(metaData.getTableName(i), metaData.getColumnName(i), metaData.getColumnLabel(i)), resultSet.getObject(i));
                }
                result.add(map);
            } catch (SQLException e) {
                throw new DatabaseNegotiationException("Failed to get the next row", e);
            }

        }
        return result;
    }

    private synchronized <H> List<? extends H> select(final H binding, boolean distinct) {
        final SelectQueryExecution<E, H> selection = selection(binding, distinct);
        final List<Map<Mapping, Object>> maps = execute(selection);
        final ArrayList<H> result = new ArrayList<H>();
        if (binding instanceof FunctionInvocation<?>) {
            for (Map<Mapping, Object> map : maps) {
                final ResolvableFunctionInvocation<Object> invocation = new ResolvableFunctionInvocation<Object>();
                //noinspection unchecked
                invocation.setInvocation((FunctionInvocation) binding);
                invocation.setResult(with(map.entrySet()).find(new Filter<Map.Entry<Mapping, Object>>() {
                    @Override
                    public boolean accepts(Map.Entry<Mapping, Object> item) {
                        return item.getKey().getLabel().equalsIgnoreCase(((FunctionInvocation) binding).getAlias());
                    }
                }).getValue());
                //noinspection unchecked
                result.add((H) invocation);
            }
            return result;
        }
        final MapEntityCreator entityCreator = new DefaultMapEntityCreator(QueryBuilderTools.getValueReaderContext());
        final QueryResultBinder<E, H> binder = new QueryResultBinder<E, H>(selection, entityCreator);
        for (Map<Mapping, Object> map : maps) {
            final H instance = QueryBuilderTools.newObject(selection.getBindingType());
            result.add(binder.bind(map, instance));
        }
        return result;
    }

    @Override
    public <H> List<? extends H> select(H binding) {
        return select(binding, false);
    }

    @Override
    public List<? extends E> select() {
        return select(null);
    }

    @Override
    public <H> List<? extends H> selectDistinct(H binding) {
        return select(binding, true);
    }

    @Override
    public List<? extends E> selectDistinct() {
        return selectDistinct(null);
    }

    @Override
    public <H> SelectQueryExecution<E, H> selection(H binding) {
        return selection(binding, false);
    }

    @Override
    public SelectQueryExecution<E, E> selection() {
        return selection(null);
    }

    @Override
    public <H> SelectQueryExecution<E, H> distinctSelection(H binding) {
        return selection(binding, true);
    }

    @Override
    public SelectQueryExecution<E, E> distinctSelection() {
        return distinctSelection(null);
    }

    public void addGroupByColumn(Object property) {
        executionBuilder.addGroupByColumn(property);
    }

    protected  void addCrossReference(SelectQueryExecution<?, ?> query, CrossReferenceType type) {
        addParameter(query, false);
        executionBuilder.addCrossReference(query, type);
    }

    protected void addOrdering(Order order) {
        executionBuilder.addOrdering(order);
    }

    protected void addHavingToken(Token token) {
        executionBuilder.addHavingToken(token);
    }

    protected void addCriteriaToken(Token token) {
        executionBuilder.addCriteriaToken(token);
    }

    DataAccessSession getSession() {
        return session;
    }

    protected <G, F> void addJoinedSource(JoinedSelectionSource<G, F> source) {
        executionBuilder.addJoinedSource(source);
    }

    protected  <F> ColumnMetadata getColumn(F property) {
        return executionBuilder.getColumn(property);
    }

    protected  <F, G> void addParameter(F source, G target) {
        final ColumnMetadata column = target == null ? null : getColumn(target);
        final Integer sqlType = column == null ? null : column.getType();
        executionBuilder.addParameter(source, sqlType);
    }

    protected void addLimitation(int pageSize, int pageNumber) {
        executionBuilder.addLimitation(pageSize, pageNumber);
    }

}
