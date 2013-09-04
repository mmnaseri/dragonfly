package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.StatementPreparator;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.statement.impl.model.ParameterPlaceholderNamespace;
import com.agileapes.dragonfly.tools.ColumnPropertyFilter;
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

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 17:02)
 */
public class DefaultStatementPreparator implements StatementPreparator {

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
        System.out.println(">> PREPARED STATEMENT:");
        System.out.println(writer);
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(writer.toString(), Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException ignored) {
        }
        assert preparedStatement != null;
        final List<String> parameters = namespace.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            final String parameter = parameters.get(i);
            try {
                if (value.containsKey(parameter)) {
                    System.out.println(">> SETTING VALUE FOR " + (i + 1) + " (" + parameter + ")");
                    preparedStatement.setObject(i + 1, value.get(parameter));
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

}
