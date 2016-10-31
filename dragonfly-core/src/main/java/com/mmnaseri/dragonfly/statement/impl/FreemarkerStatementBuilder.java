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

import com.mmnaseri.dragonfly.dialect.DatabaseDialect;
import com.mmnaseri.dragonfly.error.StatementPreparationError;
import com.mmnaseri.dragonfly.metadata.Metadata;
import com.mmnaseri.dragonfly.metadata.TableMetadata;
import com.mmnaseri.dragonfly.statement.Statement;
import com.mmnaseri.dragonfly.statement.StatementBuilder;
import com.mmnaseri.dragonfly.statement.StatementType;
import com.mmnaseri.dragonfly.statement.impl.model.FreemarkerStatementModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;

import java.io.IOException;
import java.io.StringWriter;

/**
 * This builder expands the given statement and create a final statement based on the given Freemarker
 * template. Do note that the final product of this class might be dynamic and need a second pass.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/1, 1:25)
 */
public class FreemarkerStatementBuilder implements StatementBuilder {

    private final String templateName;
    private final Configuration configuration;
    private final DatabaseDialect dialect;

    public FreemarkerStatementBuilder(Configuration configuration, String templateName, DatabaseDialect dialect) {
        this.templateName = templateName;
        this.configuration = configuration;
        this.dialect = dialect;
    }

    @Override
    public Statement getStatement(TableMetadata<?> tableMetadata, Metadata metadata) {
        final Template template;
        try {
            template = configuration.getTemplate(templateName);
        } catch (IOException e) {
            throw new StatementPreparationError("Failed to load template: " + templateName, e);
        }
        final FreemarkerStatementModel model;
        try {
            model = new FreemarkerStatementModel(tableMetadata, dialect);
        } catch (TemplateModelException ignored) {
            return null;
        }
        model.introduce("metadata", metadata);
        final StringWriter writer = new StringWriter();
        try {
            template.process(model, writer);
        } catch (Exception ignored) {
        }
        final String sql = writer.toString().trim();
        return new ImmutableStatement(tableMetadata, dialect, sql, ImmutableStatement.STATEMENT_PATTERN.matcher(sql).find(), ImmutableStatement.VALUE_PATTERN.matcher(sql).find(), StatementType.getStatementType(sql));
    }

    @Override
    public Statement getStatement(TableMetadata<?> tableMetadata) {
        return getStatement(tableMetadata, null);
    }

}
