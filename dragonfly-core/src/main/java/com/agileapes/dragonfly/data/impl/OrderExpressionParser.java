/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.data.impl;

import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.strings.document.DocumentReader;
import com.agileapes.couteau.strings.document.impl.DefaultDocumentReader;
import com.agileapes.dragonfly.annotations.Ordering;
import com.agileapes.dragonfly.error.ExpressionParseError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.OrderMetadata;
import com.agileapes.dragonfly.metadata.ResultOrderMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.impl.DefaultOrderMetadata;
import com.agileapes.dragonfly.metadata.impl.DefaultResultOrderMetadata;
import com.agileapes.dragonfly.tools.ColumnNameFilter;
import com.agileapes.dragonfly.tools.ColumnPropertyFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

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
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
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
            ordering.add(new DefaultOrderMetadata(column, Ordering.getOrdering(order)));
        }
        if (ordering.isEmpty()) {
            for (ColumnMetadata columnMetadata : tableMetadata.getPrimaryKey().getColumns()) {
                ordering.add(new DefaultOrderMetadata(columnMetadata, DEFAULT_ORDER));
            }
        }
        return new DefaultResultOrderMetadata(ordering);
    }
    
}
