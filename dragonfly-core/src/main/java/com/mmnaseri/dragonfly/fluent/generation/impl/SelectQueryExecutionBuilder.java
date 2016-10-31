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

import com.mmnaseri.couteau.basics.api.Transformer;
import com.mmnaseri.couteau.basics.api.impl.NullFilter;
import com.mmnaseri.couteau.reflection.beans.BeanAccessor;
import com.mmnaseri.couteau.reflection.beans.BeanWrapper;
import com.mmnaseri.couteau.reflection.beans.impl.MethodBeanAccessor;
import com.mmnaseri.couteau.reflection.beans.impl.MethodBeanWrapper;
import com.mmnaseri.couteau.reflection.error.NoSuchPropertyException;
import com.mmnaseri.dragonfly.data.DataAccessSession;
import com.mmnaseri.dragonfly.fluent.SelectQueryExecution;
import com.mmnaseri.dragonfly.fluent.error.BindingInitializationException;
import com.mmnaseri.dragonfly.fluent.tools.QueryBuilderTools;
import com.mmnaseri.dragonfly.metadata.ColumnMetadata;
import com.mmnaseri.dragonfly.metadata.PagingMetadata;
import com.mmnaseri.dragonfly.metadata.RelationMetadata;
import com.mmnaseri.dragonfly.metadata.TableMetadata;
import com.mmnaseri.dragonfly.metadata.impl.ImmutablePagingMetadata;
import com.mmnaseri.dragonfly.tools.DatabaseUtils;
import com.mmnaseri.dragonfly.fluent.generation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/10 AD, 12:31)
 */
public class SelectQueryExecutionBuilder<E> {

    private static final AtomicLong ALIAS_COUNTER = new AtomicLong(0);
    public static final String SELECT = "SELECT";
    public static final String DISTINCT = "DISTINCT";
    public static final String FROM = "FROM";
    public static final String AS = "AS";
    public static final String GROUP_BY = "GROUP BY";
    public static final String ORDER_BY = "ORDER BY";
    public static final String UNION = "UNION";
    public static final String ALL = "ALL";
    public static final String WHERE = "WHERE";
    public static final String HAVING = "HAVING";
    public static final String INTERSECT = "INTERSECT";
    public static final String EXCEPT = "EXCEPT";
    private SelectionSource<E> mainSource;
    private final List<JoinedSelectionSource<?, ?>> joinedSources = new ArrayList<JoinedSelectionSource<?, ?>>();
    private final List<Object> groupByColumns = new ArrayList<Object>();
    private final List<CrossReferenceMetadata<?, ?>> crossReferences = new ArrayList<CrossReferenceMetadata<?, ?>>();
    private final List<Order> orderings = new ArrayList<Order>();
    private final Expression criteria = new PrefixedExpression(WHERE);
    private final Expression having = new PrefixedExpression(HAVING);
    private final Map<Object, String> tableAliases = new HashMap<Object, String>();
    private final List<ParameterDescriptor> parameters = new ArrayList<ParameterDescriptor>();
    private final Map<Object, String> columnAliases = new HashMap<Object, String>();
    private final Map<Object, ColumnMetadata> columns = new HashMap<Object, ColumnMetadata>();
    private PagingMetadata pagingMetadata = null;

    private void addAlias(SelectionSource<?> source) {
        tableAliases.put(source.getBookKeeper().getEntity(), "t" + ALIAS_COUNTER.incrementAndGet());
    }

    private void removeAlias(SelectionSource<?> source) {
        tableAliases.remove(source.getBookKeeper().getEntity());
    }

    private String getAlias(SelectionSource<?> source) {
        for (Map.Entry<Object, String> entry : tableAliases.entrySet()) {
            if (entry.getKey() == source.getBookKeeper().getEntity()) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void setMainSource(SelectionSource<E> mainSource) {
        if (this.mainSource != null) {
            removeAlias(mainSource);
        }
        addAlias(mainSource);
        this.mainSource = mainSource;
    }

    public void addJoinedSource(JoinedSelectionSource<?, ?> source) {
        joinedSources.add(source);
        addAlias(source);
    }

    public List<SelectionSource<?>> getSources() {
        final List<SelectionSource<?>> sources = new ArrayList<SelectionSource<?>>();
        if (mainSource != null) {
            sources.add(mainSource);
        }
        for (JoinedSelectionSource<?, ?> joinedSource : joinedSources) {
            sources.add(joinedSource);
        }
        return sources;
    }

    public void addGroupByColumn(Object columnMetadata) {
        groupByColumns.add(columnMetadata);
    }


    public <H> void addCrossReference(SelectQueryExecution<H, ?> query, CrossReferenceType type) {
        //noinspection unchecked
        crossReferences.add(new ImmutableCrossReferenceMetadata<H, Object>((SelectQueryExecution<H, Object>) query, type));
    }

    public void addOrdering(Order order) {
        orderings.add(order);
    }

    private void addToken(Expression expression, Token token) {
        if (token.getType().equals(TokenType.PARENTHESIS_OPEN)) {
            expression.openParenthesis();
        } else if (token.getType().equals(TokenType.PARENTHESIS_CLOSE)) {
            expression.closeParenthesis();
        } else {
            expression.addToken(token);
        }
    }

    public void addHavingToken(Token token) {
        addToken(having, token);
    }

    public void addCriteriaToken(Token token) {
        addToken(criteria, token);
    }

    public <F> ColumnMetadata getColumn(F property) {
        final List<SelectionSource<?>> sources = getSources();
        for (SelectionSource<?> source : sources) {
            ColumnMetadata columnMetadata = source.getBookKeeper().getColumn(property);
            if (columnMetadata != null) {
                return columnMetadata;
            }
        }
        return null;
    }

    public <H> SelectQueryExecution<E, H> build(final DataAccessSession session, H binding, boolean distinct) {
        final List<SelectionSource<?>> sources = getSources();
        final ValueResolver valueResolver = new DefaultValueResolver(session, sources, tableAliases);
        Class bindingType;
        if (binding == null) {
            bindingType = mainSource.getBookKeeper().getTable().getEntityType();
            binding = createBinding();
        } else {
            bindingType = binding.getClass();
            if (bindingType.getEnclosingClass() != null) {
                bindingType = bindingType.getSuperclass();
            }
            if (FunctionInvocation.class.isAssignableFrom(bindingType)) {
                bindingType = ResolvableFunctionInvocation.class;
            }
        }
        final Map<String, Object> bindingMap = QueryBuilderTools.unwrap(binding);
        final List<String> projection = getProjection(binding, bindingMap, sources, valueResolver, session);
        final StringBuilder sql = new StringBuilder();
        sql.append(SELECT).append(" ");
        if (distinct) {
            sql.append(DISTINCT).append(" ");
        }
        if (projection.isEmpty()) {
            sql.append("*");
        } else {
            sql.append(with(projection).join(", "));
        }
        sql.append(" ").append(FROM).append(" ");
        sql.append(DatabaseUtils.qualifyTable(mainSource.getBookKeeper().getTable(), session.getDatabaseDialect()));
        sql.append(" ").append(AS).append(" ");
        sql.append(session.getDatabaseDialect().getIdentifierEscapeCharacter()).append(getAlias(mainSource)).append(session.getDatabaseDialect().getIdentifierEscapeCharacter());
        if (!joinedSources.isEmpty()) {
            sql.append(with(joinedSources).transform(new Transformer<JoinedSelectionSource<?, ?>, String>() {
                @Override
                public String map(JoinedSelectionSource<?, ?> source) {
                    final StringBuilder builder = new StringBuilder();
                    builder.append(" ");
                    builder.append(source.getJoinType().getOperator());
                    builder.append(" ");
                    builder.append(DatabaseUtils.qualifyTable(source.getBookKeeper().getTable(), session.getDatabaseDialect()));
                    builder.append(" ");
                    builder.append(AS);
                    builder.append(" ");
                    builder.append(session.getDatabaseDialect().getIdentifierEscapeCharacter());
                    builder.append(getAlias(source));
                    builder.append(session.getDatabaseDialect().getIdentifierEscapeCharacter());
                    final Object joinSource = source.getJoinSource();
                    final Object joinTarget = source.getJoinTarget();
                    if (joinSource != null || joinTarget != null) {
                        builder.append(" ON (").append(valueResolver.resolve(joinSource, false, true)).append(" ").append(source.getComparisonType().getOperator()).append(" ").append(valueResolver.resolve(joinTarget, false, true)).append(")");
                    }
                    return builder.toString();
                }
            }).join(" "));
        }
        sql.append(criteria.evaluate(valueResolver));
        if (!groupByColumns.isEmpty()) {
            sql.append(" ").append(GROUP_BY).append(" ");
            //noinspection unchecked
            sql.append(with(groupByColumns).transform(new Transformer<Object, String>() {
                @Override
                public String map(Object input) {
                    return valueResolver.resolve(input, false, true);
                }
            }).drop(new NullFilter<String>()).join(", "));
        }
        sql.append(having.evaluate(valueResolver));
        if (!orderings.isEmpty()) {
            sql.append(" ").append(ORDER_BY).append(" ");
            sql.append(with(orderings).transform(new Transformer<Order, String>() {
                @Override
                public String map(Order input) {
                    return valueResolver.resolve(input.getProperty(), false, true) + " " + input.getOrdering().toString();
                }
            }).join(", "));
        }
        for (CrossReferenceMetadata<?, ?> crossReference : crossReferences) {
            if (CrossReferenceType.UNION.equals(crossReference.getType())) {
                sql.append(" ").append(UNION);
            } else if (CrossReferenceType.UNION_ALL.equals(crossReference.getType())) {
                sql.append(" ").append(UNION).append(" ").append(ALL);
            } else if (CrossReferenceType.INTERSECT.equals(crossReference.getType())) {
                sql.append(" ").append(INTERSECT);
            } else if (CrossReferenceType.EXCEPT.equals(crossReference.getType())) {
                sql.append(" ").append(EXCEPT);
            }
            sql.append(" (");
            sql.append(crossReference.getQueryExecution().getSql());
            sql.append(")");
        }
        String query = sql.toString();
        if (pagingMetadata != null) {
            query = session.getDatabaseDialect().getPagingDecorator().decorate(query, pagingMetadata.getPageSize(), pagingMetadata.getPageNumber());
        }
        query = query.replaceAll(";+\\s*$", ";");
        //noinspection unchecked
        return new ImmutableSelectQueryExecution<E, H>(bindingType, binding, query, resolveParameters(parameters), mainSource, joinedSources, tableAliases, columnAliases, columns);
    }

    private List<ParameterDescriptor> resolveParameters(List<ParameterDescriptor> parameters) {
        final ArrayList<ParameterDescriptor> resolved = new ArrayList<ParameterDescriptor>();
        for (ParameterDescriptor parameter : parameters) {
            if (parameter.getValue() instanceof SelectQueryExecution<?, ?>) {
                final List<ParameterDescriptor> result = resolveParameters(((SelectQueryExecution<?, ?>) parameter.getValue()).getParameters());
                for (ParameterDescriptor descriptor : result) {
                    resolved.add(new ImmutableParameterDescriptor(resolved.size() + 1, descriptor.getValue(), descriptor.getSqlType()));
                }
            } else {
                resolved.add(new ImmutableParameterDescriptor(resolved.size() + 1, parameter.getValue(), parameter.getSqlType()));
            }
        }
        return resolved;
    }

    private <H> List<String> getProjection(H binding, Map<String, Object> bindingMap, List<SelectionSource<?>> sources, ValueResolver valueResolver, DataAccessSession session) {
        final Character escapeCharacter = session.getDatabaseDialect().getIdentifierEscapeCharacter();
        final Set<String> projection = new HashSet<String>();
        if (binding instanceof FunctionInvocation<?>) {
            return with(valueResolver.resolve(binding, true, true)).list();
        }
        for (SelectionSource<?> source : sources) {
            if (source.getBookKeeper().getEntity() == binding) {
                addProjectionSource(valueResolver, projection, escapeCharacter, source);
                return with(projection).sort().list();
            }
            final String resolved = valueResolver.resolve(binding, true, true);
            if (resolved != null) {
                addProjectionColumn(projection, escapeCharacter, binding, resolved);
                return with(projection).sort().list();
            }
        }
        for (Map.Entry<String, Object> entry : bindingMap.entrySet()) {
            boolean handled = false;
            for (SelectionSource<?> source : sources) {
                if (source.getBookKeeper().getEntity() == entry.getValue()) {
                    addProjectionSource(valueResolver, projection, escapeCharacter, source);
                    handled = true;
                    break;
                }
            }
            if (handled) {
                continue;
            }
            String resolved = valueResolver.resolve(entry.getValue(), true, true);
            if (resolved != null) {
                addProjectionColumn(projection, escapeCharacter, entry.getValue(), resolved);
            }
        }
        return with(projection).sort().list();
    }

    private void addProjectionColumn(Set<String> projection, Character escapeCharacter, Object value, String resolved) {
        final ColumnMetadata column = getColumn(value);
        if (column != null) {
            final SelectionSource<?> source = getSource(value);
            final String columnAlias = getAlias(source) + "_" + ALIAS_COUNTER.getAndIncrement();
            columnAliases.put(value, columnAlias);
            columns.put(value, column);
            resolved += " AS " + escapeCharacter + columnAlias + escapeCharacter;
        }
        projection.add(resolved);
    }

    private void addProjectionSource(ValueResolver valueResolver, Set<String> projection, Character escapeCharacter, SelectionSource<?> source) {
        for (Object value : source.getBookKeeper().getValues()) {
            String resolved = valueResolver.resolve(value, true, true);
            if (resolved != null) {
                final ColumnMetadata column = getColumn(value);
                if (column != null) {
                    final String columnAlias = getAlias(source) + "_" + ALIAS_COUNTER.getAndIncrement();
                    columnAliases.put(value, columnAlias);
                    columns.put(value, column);
                    resolved += " AS " + escapeCharacter + columnAlias + escapeCharacter;
                }
                projection.add(resolved);
            }
        }
    }

    private SelectionSource<?> getSource(Object property) {
        if (mainSource.getBookKeeper().getColumn(property) != null) {
            return mainSource;
        }
        for (JoinedSelectionSource<?, ?> source : joinedSources) {
            if (source.getBookKeeper().getColumn(property) != null) {
                return source;
            }
        }
        return null;
    }

    private <H> H createBinding() {
        final E entity = mainSource.getBookKeeper().getEntity();
        final TableMetadata<E> tableMetadata = mainSource.getBookKeeper().getTable();
        final Class<E> entityType = tableMetadata.getEntityType();
        final E binding = QueryBuilderTools.newObject(entityType);
        final BeanWrapper<E> wrapper = new MethodBeanWrapper<E>(binding);
        final BeanAccessor<E> accessor = new MethodBeanAccessor<E>(entity);
        for (ColumnMetadata columnMetadata : tableMetadata.getColumns()) {
            final String propertyName = columnMetadata.getPropertyName();
            try {
                if (!accessor.hasProperty(propertyName) || !wrapper.hasProperty(propertyName) || !wrapper.isWritable(propertyName)) {
                    continue;
                }
                if (columnMetadata.getForeignReference() != null) {
                    final Object found = getEntity(columnMetadata.getForeignReference().getTable().getEntityType());
                    if (found != null) {
                        wrapper.setPropertyValue(propertyName, found);
                        continue;
                    }
                }
                wrapper.setPropertyValue(propertyName, accessor.getPropertyValue(columnMetadata.getPropertyName()));
            } catch (Exception e) {
                throw new BindingInitializationException("Failed to initialize property " + propertyName, e);
            }
        }
        for (RelationMetadata<E, ?> relationMetadata : tableMetadata.getForeignReferences()) {
            if (relationMetadata.isOwner() || relationMetadata.getType().getForeignCardinality() > 1) { //means one-to-one on a non-owner entity
                continue;
            }
            final String propertyName = relationMetadata.getPropertyName();
            if (!accessor.hasProperty(propertyName) || !wrapper.hasProperty(propertyName)) {
                continue;
            }
            final Class<?> propertyType;
            try {
                propertyType = accessor.getPropertyType(propertyName);
            } catch (NoSuchPropertyException ignored) {
                //this never happens
                continue;
            }
            Object found = getEntity(propertyType);
            if (found == null) {
                continue;
            }
            try {
                wrapper.setPropertyValue(propertyName, found);
            } catch (Exception e) {
                throw new BindingInitializationException("Failed to set property " + propertyName, e);
            }
        }
        //noinspection unchecked
        return (H) binding;
    }

    private <F> F getEntity(Class<F> entityType) {
        final List<SelectionSource<?>> sources = getSources();
        for (SelectionSource<?> source : sources) {
            final Object foundEntity = source.getBookKeeper().getEntity();
            if (entityType.isInstance(foundEntity)) {
                //noinspection unchecked
                return (F) foundEntity;
            }
        }
        return null;
    }

    public <F> void addParameter(F value, Integer sqlType) {
        parameters.add(new ImmutableParameterDescriptor(parameters.size() + 1, value, sqlType));
    }

    public void addLimitation(int pageSize, int pageNumber) {
        pagingMetadata = new ImmutablePagingMetadata(pageSize, pageNumber);
    }

}
