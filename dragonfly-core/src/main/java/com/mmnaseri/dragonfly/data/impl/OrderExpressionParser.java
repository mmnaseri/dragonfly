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

package com.mmnaseri.dragonfly.data.impl;

import com.mmnaseri.couteau.basics.api.Transformer;
import com.mmnaseri.couteau.strings.document.DocumentReader;
import com.mmnaseri.couteau.strings.document.impl.DefaultDocumentReader;
import com.mmnaseri.dragonfly.annotations.Ordering;
import com.mmnaseri.dragonfly.error.ExpressionParseError;
import com.mmnaseri.dragonfly.metadata.ColumnMetadata;
import com.mmnaseri.dragonfly.metadata.OrderMetadata;
import com.mmnaseri.dragonfly.metadata.ResultOrderMetadata;
import com.mmnaseri.dragonfly.metadata.TableMetadata;
import com.mmnaseri.dragonfly.metadata.impl.DefaultResultOrderMetadata;
import com.mmnaseri.dragonfly.metadata.impl.ImmutableOrderMetadata;
import com.mmnaseri.dragonfly.tools.ColumnNameFilter;
import com.mmnaseri.dragonfly.tools.ColumnPropertyFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * <p>This is a parser that will turn a string describing an ordering based on a given
 * table metadata into actual metadata for the ordering.</p>
 *
 * <p>The syntax for the expression is:</p>
 *
 * <ul>
 *     <li><code>ordering   ::= [order-item [, order-item]*]</code></li>
 *     <li><code>order-item ::= property-or-column [ASC|DESC]</code></li>
 * </ul>
 *
 * <p>An empty string means that the ordering will be done based on the table's primary key
 * columns in the order they were defined.</p>
 *
 * <p>Not specifying whether or not the items should be ordered in ascending order, tells the
 * parser to fall back to the default order (ASC).</p>
 *
 * <p>Column names always take precedence over property names. This means that if we have a
 * column named {@code x} and a property named as such, too, specifying an order for {@code x}
 * will tell the parser to designate the ordering for the column {@code x}.</p>
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/11/3, 16:15)
 */
public class OrderExpressionParser implements Transformer<String, ResultOrderMetadata> {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern IDENTIFIER = Pattern.compile("[a-z_][a-z0-9_]*", Pattern.CASE_INSENSITIVE);
    private static final Pattern ORDER = Pattern.compile("ASC|DESC", Pattern.CASE_INSENSITIVE);
    private static final Ordering DEFAULT_ORDER = Ordering.ASCENDING;
    private static final String COMMA = ",";
    private final TableMetadata<?> tableMetadata;

    public OrderExpressionParser(TableMetadata<?> tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    @Override
    public ResultOrderMetadata map(String input) {
        final DocumentReader reader = new DefaultDocumentReader(input.trim());
        final List<OrderMetadata> ordering = new ArrayList<OrderMetadata>();
        while (reader.hasMore()) {
            final String identifier = reader.expect(IDENTIFIER, true);
            reader.skip(WHITESPACE);
            final String order;
            if (!reader.has(COMMA)) {
                order = reader.expect(ORDER, false);
                reader.skip(WHITESPACE);
            } else {
                order = DEFAULT_ORDER.toString();
            }
            if (reader.hasMore()) {
                if (reader.has(COMMA)) {
                    reader.expect(COMMA, false);
                    reader.skip(WHITESPACE);
                }
            }
            ColumnMetadata column = with(tableMetadata.getColumns()).find(new ColumnNameFilter(identifier));
            if (column == null) {
                column = with(tableMetadata.getColumns()).find(new ColumnPropertyFilter(identifier));
            }
            if (column == null) {
                throw new ExpressionParseError("No such column: " + tableMetadata.getName() + "." + identifier);
            }
            ordering.add(new ImmutableOrderMetadata(column, Ordering.getOrdering(order)));
        }
        if (ordering.isEmpty()) {
            for (ColumnMetadata columnMetadata : tableMetadata.getPrimaryKey().getColumns()) {
                ordering.add(new ImmutableOrderMetadata(columnMetadata, DEFAULT_ORDER));
            }
        }
        return new DefaultResultOrderMetadata(ordering);
    }
    
}
