package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.StatementPreparator;
import com.agileapes.dragonfly.query.impl.functions.ParameterPlaceholderNamespace;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 17:02)
 */
public class DefaultStatementPreparator implements StatementPreparator {

    @Override
    public PreparedStatement prepare(Connection connection, String sql, Map<String, Object> value) {
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
            preparedStatement = connection.prepareStatement(writer.toString());
        } catch (SQLException ignored) {
        }
        assert preparedStatement != null;
        final List<String> parameters = namespace.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            final String parameter = parameters.get(i);
            if (value.containsKey(parameter)) {
                try {
                    System.out.println(">> SETTING VALUE FOR " + (i + 1) + " (" + parameter + ")");
                    preparedStatement.setObject(i + 1, value.get(parameter));
                } catch (SQLException ignored) {
                }
            }
        }
        return preparedStatement;
    }

}
