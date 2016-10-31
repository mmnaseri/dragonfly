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

package com.agileapes.dragonfly.tools;

import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.impl.TableMetadataCopier;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * This class defines a set of utilities that will be used empirically throughout the framework for
 * enforcing various conventions.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:13)
 */
public abstract class DatabaseUtils {

    /**
     * Will shorten the given name to the specified length, while trying to keep it as resembling the original
     * identifier as possible. For generic purposes using {@link #unify(String)} is preferable.
     * @param name      the original name
     * @param length    the length to which the name must be shortened
     * @return the processed name
     */
    public static String shorten(final String name, int length) {
        String identifier = name;
        while (identifier.length() > length) {
            String original = identifier;
            identifier = identifier.replaceFirst("[aeiou]", "");
            if (identifier.equals(original)) {
                break;
            }
        }
        while (identifier.length() > length) {
            String original = identifier;
            identifier = identifier.replaceFirst("([a-z])(.*?)\\1", "$1$2");
            if (identifier.equals(original)) {
                break;
            }
        }
        if (identifier.length() > length) {
            identifier = identifier.substring(0, length);
        }
        return identifier;
    }

    /**
     * Applies a unified transformation to any given database identifier. This includes shortening the identifier to
     * anything shorter than 25 characters long and making it a lower-case string
     * @param name    the identifier that should be unified
     * @return the unified identifier name.
     */
    public static String unify(final String name) {
        return shorten(name, 25).toLowerCase();
    }

    /**
     * Given two tables involved in a many-to-many relation on two specific properties (one from each side) this method
     * will generate a deterministic name for the table which would contain the relationship between the two. The order
     * of the tables and their properties are immaterial.
     * @param first             the first table
     * @param second            the second table.
     * @param firstProperty     the first property (from one table)
     * @param secondProperty    the second property (from the other table)
     * @return the name of the middle table
     */
    public static String getMiddleTableName(TableMetadata<?> first, TableMetadata<?> second, String firstProperty, String secondProperty) {
        final String tableNames = with(first.getName(), second.getName()).sort().join("_");
        final String finalName = with(tableNames).add(with(firstProperty, secondProperty).sort().list()).join("_");
        return shorten(finalName, 25).toLowerCase();
    }

    /**
     * Creates the qualified name of the table
     * @param tableMetadata          table metadata
     * @param identifierQuotation    the quotation around each identifier
     * @param schemaSeparator        the schema separator used by the dialect
     * @return the qualified name
     */
    public static String qualifyTable(TableMetadata<?> tableMetadata, Character identifierQuotation, Character schemaSeparator) {
        final String name = identifierQuotation + tableMetadata.getName() + identifierQuotation;
        return tableMetadata.getSchema() == null || tableMetadata.getSchema().isEmpty() ? name : identifierQuotation + tableMetadata.getSchema() + identifierQuotation + schemaSeparator + name;
    }

    /**
     * Same as {@link #qualifyTable(com.agileapes.dragonfly.metadata.TableMetadata, Character, Character)}, only it retrieves
     * the specifics of the qualification from a given database dialect
     * @param tableMetadata      table metadata
     * @param databaseDialect    the database dialect in use
     * @return the qualified name
     */
    public static String qualifyTable(TableMetadata<?> tableMetadata, DatabaseDialect databaseDialect) {
        return qualifyTable(tableMetadata, databaseDialect.getIdentifierEscapeCharacter(), databaseDialect.getSchemaSeparator());
    }

    /**
     * Same as {@link #qualifyTable(com.agileapes.dragonfly.metadata.TableMetadata, Character, Character)}, only for columns
     * @param columnMetadata     column metadata
     * @param databaseDialect    the database dialect in use
     * @return the qualified name
     */
    public static String qualifyColumn(ColumnMetadata columnMetadata, DatabaseDialect databaseDialect) {
        final Character schemaSeparator = databaseDialect.getSchemaSeparator();
        final Character identifierQuotation = databaseDialect.getIdentifierEscapeCharacter();
        return identifierQuotation + columnMetadata.getTable().getName() + identifierQuotation + schemaSeparator + identifierQuotation + columnMetadata.getName() + identifierQuotation;
    }

    /**
     * Escapes a given string literal for use within quotations inside a database query
     * @param string                   the string literal to be escaped
     * @param stringEscapeCharacter    the character used for escaping (normally '\')
     * @return the escaped string
     */
    public static String escapeString(String string, Character stringEscapeCharacter) {
        final String escapeCharacter = String.valueOf('\\' == stringEscapeCharacter ? "\\\\" : stringEscapeCharacter);
        return string.replace("\n", "\\n").replaceAll("(^|[^" + escapeCharacter + "])\"", "$1" + escapeCharacter + "\"");
    }

    /**
     * Copies the entire data structure of the table
     * @param table    source table
     * @return copied instance
     */
    public static <E> TableMetadata<E> copyTable(TableMetadata<E> table) {
        return new TableMetadataCopier<E>(table).copy();
    }

    /**
     * Copies the entire data structure of the column, even its table so that no two references are shared between the
     * column being passed through and the one returned
     * @param column    source column
     * @return copied instance
     */
    public static ColumnMetadata copyColumn(ColumnMetadata column) {
        //noinspection unchecked
        return (ColumnMetadata) with(new TableMetadataCopier(column.getTable()).copy().getColumns()).find(new ColumnNameFilter(column.getName()));
    }

}
