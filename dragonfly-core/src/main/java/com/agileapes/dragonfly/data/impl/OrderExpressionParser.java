package com.agileapes.dragonfly.data.impl;

import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.couteau.strings.document.DocumentReader;
import com.agileapes.couteau.strings.document.impl.DefaultDocumentReader;
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
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/11/3, 16:15)
 */
public class OrderExpressionParser implements Transformer<String, ResultOrderMetadata> {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern IDENTIFIER = Pattern.compile("[a-z_][a-z0-9_]*", Pattern.CASE_INSENSITIVE);
    private static final Pattern ORDER = Pattern.compile("ASC|DESC", Pattern.CASE_INSENSITIVE);
    private static final String DEFAULT_ORDER = "ASC";
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
                order = DEFAULT_ORDER;
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
            ordering.add(new DefaultOrderMetadata(column, order));
        }
        return new DefaultResultOrderMetadata(ordering);
    }
    
}
