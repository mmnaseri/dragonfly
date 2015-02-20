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

package com.agileapes.dragonfly.statement;

/**
 * Determines the generic type of the manipulation statement as recognized by the framework.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/4, 18:09)
 */
public enum StatementType {

    /**
     * Returns results by asking the database for a specific range of data items
     */
    QUERY("SELECT"),
    /**
     * Adds a new element to the database
     */
    INSERT("INSERT"),
    /**
     * Updates some elements in the database
     */
    UPDATE("UPDATE"),
    /**
     * Deletes some elements from the database
     */
    DELETE("DELETE"),
    /**
     * Attempts to truncate an entire table
     */
    TRUNCATE("TRUNCATE"),
    /**
     * Makes a call to a stored procedure
     */
    CALL("CALL"),
    /**
     * It's not a manipulation statement
     */
    DEFINITION("<none>");

    private final String starter;

    private StatementType(String starter) {
        this.starter = starter;
    }

    /**
     * Returns the type of the statement as determined from the SQL being passed
     * @param sql    the SQL statement to be evaluated
     * @return the type of the statement
     */
    public static StatementType getStatementType(String sql) {
        if (sql.length() < 6) {
            return DEFINITION;
        }
        final String starter = sql.trim().substring(0, 6).toUpperCase();
        for (StatementType type : StatementType.values()) {
            if (type.starter.startsWith(starter)) {
                return type;
            }
        }
        if (starter.matches("CALL\\s.*")) {
            return CALL;
        }
        return DEFINITION;
    }

}
