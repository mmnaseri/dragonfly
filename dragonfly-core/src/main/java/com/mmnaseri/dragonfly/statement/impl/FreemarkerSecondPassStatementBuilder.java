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
import com.mmnaseri.dragonfly.metadata.Metadata;
import com.mmnaseri.dragonfly.metadata.TableMetadata;
import com.mmnaseri.dragonfly.statement.Statement;
import com.mmnaseri.dragonfly.statement.StatementBuilder;
import com.mmnaseri.dragonfly.statement.StatementType;
import com.mmnaseri.dragonfly.statement.impl.model.FreemarkerStatementModel;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;

import java.io.IOException;
import java.io.StringWriter;

/**
 * This class is a statement builder that will take in a pre-created statement that is in need of
 * a second processing (meaning that it is a dynamic statement) and based on the given properties
 * and parameters process it to be ready for transfer.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/4, 17:53)
 */
public class FreemarkerSecondPassStatementBuilder implements StatementBuilder {

    private final Statement statement;
    private final DatabaseDialect dialect;
    private final Object value;
    private Template template;

    public FreemarkerSecondPassStatementBuilder(Statement statement, DatabaseDialect dialect, Object value) {
        this.statement = statement;
        this.dialect = dialect;
        this.value = value;
        final StringTemplateLoader loader = new StringTemplateLoader();
        String sql = statement.getSql();
        sql = sql.replaceAll("<%(.*?)>", "<#$1>");
        sql = sql.replaceAll("</%(.*?)>", "</#$1>");
        sql = sql.replaceAll("%\\{(.*?)\\}", "\\${$1}");
        loader.putTemplate("sql", sql);
        final Configuration configuration = new Configuration();
        configuration.setTemplateLoader(loader);
        try {
            template = configuration.getTemplate("sql");
        } catch (IOException ignored) {
        }
    }

    @Override
    public Statement getStatement(TableMetadata<?> tableMetadata, Metadata metadata) {
        throw new UnsupportedOperationException();
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
        return new ImmutableStatement(tableMetadata, dialect, sql, false, statement.hasParameters(), StatementType.getStatementType(sql));
    }

}
