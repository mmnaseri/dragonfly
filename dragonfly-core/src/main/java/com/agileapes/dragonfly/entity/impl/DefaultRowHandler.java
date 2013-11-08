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

package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.RowHandler;
import com.agileapes.dragonfly.error.MetadataAccessException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class will turn a row of result set data into a key-value map to make it better accessible
 * to other parts of the program.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:31)
 */
public class DefaultRowHandler implements RowHandler {

    @Override
    public Map<String, Object> handleRow(ResultSet resultSet) {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        final ResultSetMetaData metaData;
        try {
            metaData = resultSet.getMetaData();
            final int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i ++) {
                map.put(metaData.getColumnName(i), resultSet.getObject(i));
            }
        } catch (SQLException e) {
            throw new MetadataAccessException(e);
        }
        return map;
    }

}
