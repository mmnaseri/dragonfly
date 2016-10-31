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

package com.mmnaseri.dragonfly.fluent.generation.impl;

import com.mmnaseri.couteau.basics.api.Transformer;
import com.mmnaseri.dragonfly.data.DataAccessSession;
import com.mmnaseri.dragonfly.fluent.generation.FunctionInvocation;
import com.mmnaseri.dragonfly.fluent.generation.SelectionSource;
import com.mmnaseri.dragonfly.metadata.ColumnMetadata;
import com.mmnaseri.dragonfly.fluent.generation.ValueResolver;

import java.util.List;
import java.util.Map;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (14/8/11 AD, 15:31)
 */
public class DefaultValueResolver implements ValueResolver {

    public static final String AS = "AS";
    private final DataAccessSession session;
    private final List<SelectionSource<?>> sources;
    private final Map<Object, String> tableAliases;

    public DefaultValueResolver(DataAccessSession session, List<SelectionSource<?>> sources, Map<Object, String> tableAliases) {
        this.session = session;
        this.sources = sources;
        this.tableAliases = tableAliases;
    }

    private String getAlias(SelectionSource<?> source) {
        for (Map.Entry<Object, String> entry : tableAliases.entrySet()) {
            if (entry.getKey() == source.getBookKeeper().getEntity()) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public String resolve(Object value, final boolean aliasFunction, boolean resolveKeys) {
        String result = "";
        final Character escapeCharacter = session.getDatabaseDialect().getIdentifierEscapeCharacter();
        if (value instanceof FunctionInvocation) {
            final FunctionInvocation invocation = (FunctionInvocation) value;
            result += invocation.getFunction().getFunctionName();
            result += "(";
            result += with(invocation.getArguments()).transform(new Transformer<Object, String>() {
                @Override
                public String map(Object input) {
                    return resolve(input, aliasFunction, true);
                }
            }).join(", ");
            result += ")";
            if (aliasFunction) {
                result += " " + AS + " " + escapeCharacter + invocation.getAlias() + escapeCharacter;
            }
        } else {
            for (SelectionSource<?> source : sources) {
                final String tableAlias = getAlias(source);
                final ColumnMetadata column;
                if (resolveKeys && source.getBookKeeper().getEntity() == value) {
                    column = source.getBookKeeper().getTable().getPrimaryKey().getColumns().iterator().next();
                } else {
                    column = source.getBookKeeper().getColumn(value);
                }
                if (column != null) {
                    result += escapeCharacter + tableAlias + escapeCharacter;
                    result += session.getDatabaseDialect().getSchemaSeparator();
                    result += escapeCharacter + column.getName() + escapeCharacter;
                    break;
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

}
