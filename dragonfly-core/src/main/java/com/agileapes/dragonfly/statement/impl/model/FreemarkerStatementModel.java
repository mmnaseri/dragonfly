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

package com.agileapes.dragonfly.statement.impl.model;

import com.agileapes.couteau.freemarker.model.UnresolvedMapModel;
import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.statement.impl.model.functions.*;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>This is a model used for stereotyping the statements written by both end users and throughout the data
 * allowing for efficient writing of dynamic queries.</p>
 *
 * <p>Such statements will benefit from the functions and values made available to them and can be expanded both
 * dynamically and statically.</p>
 *
 * <p>The values are:</p>
 *
 * <ul>
 *     <li><strong>dialect</strong>; which will give access to the dialect. This is an instance of {@link DatabaseDialect}</li>
 *     <li><strong>table</strong>; which is the table for which the statement will be expanded. This is an instance of
 *     {@link TableMetadata}</li>
 *     <li><strong>value</strong>; which is a map of all the values and column values accessible to the model. Note that this
 *     is applicable and expandable for queries which interact with data and not with structure.</li>
 *     <li><strong>new</strong>; the new values that are not yet represented in the database. This is also not applicable
 *     for structural queries.</li>
 *     <li><strong>old</strong>; the values currently in the database. These are essentially the same as the stuff accessible
 *     through {@code value}</li>
 * </ul>
 *
 * <p>Other than the values accessible to queries, a number of helper methods are also available which will make it much
 * easier to write universal, dynamic queries. These are:</p>
 *
 * <ul>
 *     <li><strong>isGenerated</strong>; given a column object, will determine whether or not the value for that column
 *     has a generation strategy or it should be always specified manually.</li>
 *     <li><strong>isNotGenerated</strong>; does the exact opposite of <code>isGenerated</code></li>
 *     <li><strong>isReference</strong>; determines whether or not the given column is a reference to a foreign column</li>
 *     <li><strong>isNotReference</strong>; does the exact opposite of <code>isReference</code></li>
 *     <li><strong>isSet</strong>; determines whether or not a column's value has been provided for the current query</li>
 *     <li><strong>isVersion</strong>; picks out the version columns</li>
 *     <li><strong>isNotVersion</strong>; picks out all columns other than the version column</li>
 *     <li><strong>escape</strong>; escapes the given input text according to the conventions set forth by the database
 *     dialect</li>
 *     <li><strong>quote</strong>; adds vendor-specific quotation around the given identifier to separate it from
 *     database identifiers</li>
 *     <li><strong>type</strong>; Returns vendor-specific SQL type for the given column</li>
 * </ul>
 *
 * <p>All methods starting with <code>is</code> can be applied to collections of items as well as items, which will
 * result in a filtered collection.</p>
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/1, 1:29)
 */
public class FreemarkerStatementModel implements TemplateHashModel {

    private final Map<String, TemplateModel> items = new HashMap<String, TemplateModel>();
    private final BeansWrapper wrapper;

    public FreemarkerStatementModel(TableMetadata<?> tableMetadata, DatabaseDialect dialect) throws TemplateModelException {
        this(tableMetadata, dialect, null);
    }

    public FreemarkerStatementModel(TableMetadata<?> tableMetadata, DatabaseDialect dialect, Object value) throws TemplateModelException {
        this.wrapper = BeansWrapper.getDefaultInstance();
        items.put("dialect", wrapper.wrap(dialect));
        items.put("table", wrapper.wrap(tableMetadata));
        items.put("value", new UnresolvedMapModel("value"));
        items.put("old", new UnresolvedMapModel("old"));
        items.put("new", new UnresolvedMapModel("new"));
        items.put("qualify", new DatabaseIdentifierQualifierMethod(dialect));
        items.put("isNotGenerated", new NonGeneratedColumnFilterMethod(tableMetadata));
        items.put("isGenerated", new GeneratedColumnFilterMethod(tableMetadata));
        items.put("isNotReference", new NonForeignKeyFilterMethod());
        items.put("isReference", new ForeignKeyFilterMethod());
        items.put("isSet", new ValueColumnSelectorMethod(value));
        items.put("isVersion", new VersionColumnSelectorMethod());
        items.put("isNotVersion", new NonVersionColumnSelectorMethod());
        items.put("escape", new EscapeMethod(dialect.getIdentifierEscapeCharacter()));
        items.put("quote", new QuoteMethod(dialect.getStringEscapeCharacter()));
        items.put("type", new TypeResolverMethod(dialect));
        items.put("column", new ColumnPickerMethod(tableMetadata));
    }

    public void introduce(String name, Object value) {
        try {
            items.put(name, wrapper.wrap(value));
        } catch (TemplateModelException e) {
            throw new RuntimeException("Failed to introduce value: " + name);
        }
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        return items.get(key);
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return items.isEmpty();
    }

}
