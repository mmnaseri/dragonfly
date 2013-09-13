package com.agileapes.dragonfly.entity;

import java.sql.ResultSet;
import java.util.Map;

/**
 * This interface represents the process of converting a result set to a simple map
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:14)
 */
public interface RowHandler {

    /**
     * Converts the given result set into a map
     * @param resultSet    the result set
     * @return the converted map
     */
    Map<String, Object> handleRow(ResultSet resultSet);

}
