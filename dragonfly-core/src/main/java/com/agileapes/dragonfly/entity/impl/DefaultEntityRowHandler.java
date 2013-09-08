package com.agileapes.dragonfly.entity.impl;

import com.agileapes.dragonfly.entity.EntityRowHandler;
import com.agileapes.dragonfly.error.MetadataAccessException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:31)
 */
public class DefaultEntityRowHandler implements EntityRowHandler {

    @Override
    public <E> Map<String, Object> handleRow(ResultSet resultSet) {
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
