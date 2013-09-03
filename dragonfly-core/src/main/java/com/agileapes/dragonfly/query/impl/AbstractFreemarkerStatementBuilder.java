package com.agileapes.dragonfly.query.impl;

import com.agileapes.dragonfly.error.StatementError;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.query.StatementBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/1, 1:25)
 */
public class AbstractFreemarkerStatementBuilder implements StatementBuilder {

    private final String templateName;
    private final Configuration configuration;

    public AbstractFreemarkerStatementBuilder(Configuration configuration, String templateName) {
        this.templateName = templateName;
        this.configuration = configuration;
    }

    @Override
    public String getStatement(TableMetadata<?> tableMetadata) {
        final Template template;
        try {
            template = configuration.getTemplate(templateName);
        } catch (IOException e) {
            throw new StatementError("Failed to load template: " + templateName, e);
        }
        return null;
    }

}
