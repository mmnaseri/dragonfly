package com.agileapes.dragonfly.query.impl;

import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.error.StatementError;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.query.Query;
import com.agileapes.dragonfly.query.QueryBuilder;
import com.agileapes.dragonfly.query.impl.model.FreemarkerStatementModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Pattern;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/1, 1:25)
 */
public abstract class AbstractFreemarkerQueryBuilder implements QueryBuilder {

    private final String templateName;
    private final Configuration configuration;
    private final DatabaseDialect dialect;

    public AbstractFreemarkerQueryBuilder(Configuration configuration, String templateName, DatabaseDialect dialect) {
        this.templateName = templateName;
        this.configuration = configuration;
        this.dialect = dialect;
    }

    @Override
    public Query getQuery(TableMetadata<?> tableMetadata) {
        final Template template;
        try {
            template = configuration.getTemplate(templateName);
        } catch (IOException e) {
            throw new StatementError("Failed to load template: " + templateName, e);
        }
        final FreemarkerStatementModel model;
        try {
            model = new FreemarkerStatementModel(tableMetadata, dialect);
        } catch (TemplateModelException ignored) {
            return null;
        }
        final StringWriter writer = new StringWriter();
        try {
            template.process(model, writer);
        } catch (Exception ignored) {
        }
        final String sql = writer.toString();
        return new ImmutableQuery(sql, Pattern.compile("(%\\{.*?\\}|<%.*?>|</%.*>)", Pattern.DOTALL).matcher(sql).find());
    }

}
