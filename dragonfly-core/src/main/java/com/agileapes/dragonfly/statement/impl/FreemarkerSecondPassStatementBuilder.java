package com.agileapes.dragonfly.statement.impl;

import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.statement.Statement;
import com.agileapes.dragonfly.statement.StatementBuilder;
import com.agileapes.dragonfly.statement.StatementType;
import com.agileapes.dragonfly.statement.impl.model.FreemarkerStatementModel;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;

import java.io.IOException;
import java.io.StringWriter;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/4, 17:53)
 */
public class FreemarkerSecondPassStatementBuilder implements StatementBuilder {

    private final Statement statement;
    private final DatabaseDialect dialect;
    private final Object value;
    private Template template;

    public FreemarkerSecondPassStatementBuilder(Configuration configuration, Statement statement, DatabaseDialect dialect, Object value) {
        this.statement = statement;
        this.dialect = dialect;
        this.value = value;
        final StringTemplateLoader loader = new StringTemplateLoader();
        String sql = statement.getSql();
        sql = sql.replaceAll("<%(.*?)>", "<#$1>");
        sql = sql.replaceAll("</%(.*?)>", "</#$1>");
        sql = sql.replaceAll("%\\{(.*?)\\}", "\\${$1}");
        loader.putTemplate("sql", sql);
        configuration.setTemplateLoader(loader);
        try {
            template = configuration.getTemplate("sql");
        } catch (IOException ignored) {
        }
    }

    @Override
    public Statement getStatement(TableMetadata<?> tableMetadata) {
        final FreemarkerStatementModel model;
        try {
            model = new FreemarkerStatementModel(tableMetadata, dialect, value);
        } catch (TemplateModelException ignored) {
            return null;
        }
        final StringWriter writer = new StringWriter();
        try {
            template.process(model, writer);
        } catch (Exception ignored) {
        }
        final String sql = writer.toString();
        return new ImmutableStatement(sql, false, statement.hasParameters(), StatementType.getStatementType(sql));
    }

}
