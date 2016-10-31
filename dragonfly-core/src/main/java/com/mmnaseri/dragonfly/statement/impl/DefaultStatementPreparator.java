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

import com.mmnaseri.dragonfly.metadata.ColumnMetadata;
import com.mmnaseri.dragonfly.metadata.TableMetadata;
import com.mmnaseri.dragonfly.statement.StatementPreparator;
import com.mmnaseri.dragonfly.statement.impl.model.ParameterPlaceholderNamespace;
import com.mmnaseri.dragonfly.tools.ColumnPropertyFilter;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * This class will prepare an SQL statement by interpolating property values as defined in
 * the given map, and finally returning a {@link PreparedStatement} instance through the
 * designated connection.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/3, 17:02)
 */
public class DefaultStatementPreparator implements StatementPreparator {

    private final boolean preparesCalls;

    public DefaultStatementPreparator(boolean preparesCalls) {
        this.preparesCalls = preparesCalls;
    }

    private PreparedStatement getPreparedStatement(Connection connection, StringWriter writer) {
        PreparedStatement preparedStatement = null;
        try {
            if (!preparesCalls) {
                preparedStatement = connection.prepareStatement(writer.toString(), Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareCall(writer.toString());
            }
        } catch (SQLException ignored) {
        }
        return preparedStatement;
    }

    @Override
    public PreparedStatement prepare(Connection connection, TableMetadata<?> tableMetadata, Map<String, Object> value, String sql) {
        final Configuration configuration = new Configuration();
        final StringTemplateLoader loader = new StringTemplateLoader();
        final ParameterPlaceholderNamespace namespace = new ParameterPlaceholderNamespace();
        loader.putTemplate("sql", sql);
        configuration.setTemplateLoader(loader);
        Template template = null;
        try {
            template = configuration.getTemplate("sql");
        } catch (IOException ignored) {
        }
        assert template != null;
        final StringWriter writer = new StringWriter();
        try {
            template.process(namespace, writer);
        } catch (Exception ignored) {
        }
        final PreparedStatement preparedStatement = getPreparedStatement(connection, writer);
        final List<String> parameters = namespace.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            final String parameter = parameters.get(i);
            try {
                if (value.containsKey(parameter)) {
                    Object inferredValue = value.get(parameter);
                    if (inferredValue instanceof Enum) {
                        inferredValue = ((Enum) inferredValue).name();
                    }
                    preparedStatement.setObject(i + 1, inferredValue);
                } else {
                    final String property = parameter.substring(parameter.lastIndexOf('.') + 1);
                    final ColumnMetadata metadata = with(tableMetadata.getColumns()).keep(new ColumnPropertyFilter(property)).first();
                    preparedStatement.setNull(i + 1, metadata.getType());
                }
            } catch (SQLException ignored) {
            }
        }
        return preparedStatement;
    }

    @Override
    public PreparedStatement prepare(PreparedStatement preparedStatement, TableMetadata<?> tableMetadata, Map<String, Object> value, String sql) {
        final Configuration configuration = new Configuration();
        final StringTemplateLoader loader = new StringTemplateLoader();
        final ParameterPlaceholderNamespace namespace = new ParameterPlaceholderNamespace();
        loader.putTemplate("sql", sql);
        configuration.setTemplateLoader(loader);
        Template template = null;
        try {
            template = configuration.getTemplate("sql");
        } catch (IOException ignored) {
        }
        assert template != null;
        final StringWriter writer = new StringWriter();
        try {
            template.process(namespace, writer);
        } catch (Exception ignored) {
        }
        final List<String> parameters = namespace.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            final String parameter = parameters.get(i);
            try {
                if (value.containsKey(parameter)) {
                    Object inferredValue = value.get(parameter);
                    if (inferredValue instanceof Enum) {
                        inferredValue = ((Enum) inferredValue).name();
                    }
                    preparedStatement.setObject(i + 1, inferredValue);
                } else {
                    final String property = parameter.contains(".") ? parameter.substring(parameter.lastIndexOf('.') + 1) : parameter;
                    final ColumnMetadata metadata = with(tableMetadata.getColumns()).keep(new ColumnPropertyFilter(property)).first();
                    preparedStatement.setNull(i + 1, metadata.getType());
                }
            } catch (SQLException ignored) {
            }
        }
        return preparedStatement;
    }

}
