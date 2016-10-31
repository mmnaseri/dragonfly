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

package com.mmnaseri.dragonfly.entity.impl;

import com.mmnaseri.dragonfly.entity.RowHandler;
import com.mmnaseri.dragonfly.error.ResultSetMetadataAccessError;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class will turn a row of result set data into a key-value map to make it better accessible
 * to other parts of the program.
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/8/31, 17:31)
 */
public class DefaultRowHandler implements RowHandler {

    private final boolean prefixNames;

    public DefaultRowHandler() {
        this(false);
    }

    public DefaultRowHandler(boolean prefixNames) {
        this.prefixNames = prefixNames;
    }

    @Override
    public Map<String, Object> handleRow(ResultSet resultSet) {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        final ResultSetMetaData metaData;
        try {
            metaData = resultSet.getMetaData();
            final int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i ++) {
                String key = metaData.getColumnName(i);
                if (prefixNames) {
                    key = metaData.getTableName(i) + "." + key;
                }
                map.put(key, resultSet.getObject(i));
            }
        } catch (SQLException e) {
            throw new ResultSetMetadataAccessError(e);
        }
        return map;
    }

}
