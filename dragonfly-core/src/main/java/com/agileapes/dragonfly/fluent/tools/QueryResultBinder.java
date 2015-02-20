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

package com.agileapes.dragonfly.fluent.tools;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.context.value.ValueReaderContext;
import com.agileapes.couteau.reflection.util.ClassUtils;
import com.agileapes.dragonfly.entity.MapEntityCreator;
import com.agileapes.dragonfly.error.EntityDefinitionError;
import com.agileapes.dragonfly.fluent.SelectQueryExecution;
import com.agileapes.dragonfly.fluent.generation.FunctionInvocation;
import com.agileapes.dragonfly.fluent.generation.Mapping;
import com.agileapes.dragonfly.fluent.generation.SelectionSource;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/10 AD, 12:15)
 */
public class QueryResultBinder<E, H> {


    private final SelectQueryExecution<E, H> selection;
    private final MapEntityCreator entityCreator;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ValueReaderContext readerContext;

    public QueryResultBinder(SelectQueryExecution<E, H> selection, MapEntityCreator entityCreator) {
        this.selection = selection;
        this.entityCreator = entityCreator;
        this.mapper.enableDefaultTyping();
        this.readerContext = QueryBuilderTools.getValueReaderContext();
    }

    public H bind(Map<Mapping, Object> values, H instance) {
        for (SelectionSource<?> source : selection.getSources()) {
            if (selection.getBinding() == source.getBookKeeper().getEntity()) {
                //noinspection unchecked
                return (H) bindEntity(values, source.getBookKeeper().getEntity(), getTable(selection.getBinding()));
            }
            final ColumnMetadata column = source.getBookKeeper().getColumn(selection.getBinding());
            if (column != null) {
                //noinspection unchecked
                return (H) bindColumn(values, selection.getBinding(), getColumn(selection.getBinding()));
            }
        }
        final Map<String, Object> map = QueryBuilderTools.unwrap(instance, selection.getBinding());
        for (String key : map.keySet()) {
            map.put(key, null);
        }
        if (instance instanceof List) {
            List list = (List) instance;
            while (list.size() < map.size()) {
                //noinspection unchecked
                list.add(null);
            }
        }
        final Map<String, Object> binding = QueryBuilderTools.unwrap(selection.getBinding());
        for (final Map.Entry<String, Object> entry : binding.entrySet()) {
            Object value = null;
            final Object entryValue = entry.getValue();
            if (entryValue instanceof FunctionInvocation) {
                value = with(values.entrySet()).find(new AliasFilter(((FunctionInvocation) entryValue).getAlias())).getValue();
            } else {
                final String table = getTable(entryValue);
                if (table != null) {
                    value = bindEntity(values, entryValue, table);
                }
                final String column = getColumn(entryValue);
                if (column != null) {
                    value = bindColumn(values, entryValue, column);
                }
            }
            QueryBuilderTools.setValue(instance, entry.getKey(), value);
        }
        return instance;
    }

    private Object bindColumn(Map<Mapping, Object> values, final Object entryValue, String column) {
        Object value;
        value = with(values.entrySet()).find(new AliasFilter(column)).getValue();
        final ColumnMetadata columnMetadata = with(selection.getColumns().entrySet()).find(new Filter<Map.Entry<Object, ColumnMetadata>>() {
            @Override
            public boolean accepts(Map.Entry<Object, ColumnMetadata> item) {
                return item.getKey() == entryValue;
            }
        }).getValue();
        postProcess(value, columnMetadata);
        return value;
    }

    private Object bindEntity(Map<Mapping, Object> values, final Object entryValue, String table) {
        Object value;
        final List<Map.Entry<Mapping, Object>> list = with(values.entrySet()).keep(new TableFilter(table)).list();
        final HashMap<String, Object> fetched = new HashMap<String, Object>();
        for (Map.Entry<Mapping, Object> mapping : list) {
            fetched.put(mapping.getKey().getColumnName(), mapping.getValue());
        }
        final TableMetadata<?> tableMetadata = with(selection.getSources()).find(new Filter<SelectionSource<?>>() {
            @Override
            public boolean accepts(SelectionSource<?> item) {
                return item.getBookKeeper().getEntity() == entryValue;
            }
        }).getBookKeeper().getTable();
        value = entityCreator.fromMap(QueryBuilderTools.newObject(tableMetadata.getEntityType()), tableMetadata.getColumns(), fetched);
        return value;
    }

    private Object postProcess(Object inferredValue, ColumnMetadata columnMetadata) {
        try {
            if (inferredValue == null) {
                return null;
            }
            final Class<?> propertyType = columnMetadata.getPropertyType();
            if (columnMetadata.isComplex() && inferredValue instanceof String) {
                final String[] split = ((String) inferredValue).split(";", 2);
                final Class targetType = ClassUtils.forName(split[0], getClass().getClassLoader());
                inferredValue = mapper.readValue(split[1], targetType);
            }
            if (Enum.class.isAssignableFrom(propertyType)) {
                if (!(inferredValue instanceof String)) {
                    throw new IllegalArgumentException("Expected retrieved value for an enum to be a string");
                }
                inferredValue = Enum.valueOf(propertyType.asSubclass(Enum.class), (String) inferredValue);
            } else if (Character.class.isAssignableFrom(propertyType) && inferredValue instanceof String && ((String) inferredValue).length() == 1) {
                inferredValue = ((String) inferredValue).charAt(0);
            } else if (Class.class.isAssignableFrom(propertyType) && inferredValue instanceof String && !((String) inferredValue).isEmpty()) {
                inferredValue = ClassUtils.forName((String) inferredValue, getClass().getClassLoader());
            } else if (columnMetadata.isCollection()) {
                if (!Collection.class.isAssignableFrom(propertyType)) {
                    throw new EntityDefinitionError("Expected property `" + columnMetadata.getPropertyName() + "` to be a collection but it was " + propertyType.getCanonicalName());
                }
                if (!(inferredValue instanceof String)) {
                    throw new EntityDefinitionError("Invalid property value for basic collection " + columnMetadata.getPropertyName());
                }
                inferredValue = readerContext.read((String) inferredValue, propertyType);
            }
            return inferredValue;
        } catch (Exception ignored) {}
        return null;
    }

    private String getTable(Object value) {
        for (Map.Entry<Object, String> table : selection.getTableAliases().entrySet()) {
            if (table.getKey() == value) {
                return table.getValue();
            }
        }
        return null;
    }

    private String getColumn(Object value) {
        for (Map.Entry<Object, String> column : selection.getColumnAliases().entrySet()) {
            if (column.getKey() == value) {
                return column.getValue();
            }
        }
        return null;
    }

    private static class AliasFilter implements Filter<Map.Entry<Mapping, Object>> {

        private final String alias;

        private AliasFilter(String alias) {
            this.alias = alias;
        }

        @Override
        public boolean accepts(Map.Entry<Mapping, Object> item) {
            return item.getKey().getLabel().equalsIgnoreCase(alias);
        }
    }

    private static class TableFilter implements Filter<Map.Entry<Mapping, Object>> {

        private final String table;

        private TableFilter(String table) {
            this.table = table;
        }

        @Override
        public boolean accepts(Map.Entry<Mapping, Object> item) {
            return item.getKey().getLabel().toLowerCase().startsWith(table.toLowerCase() + "_");
        }
    }

}
