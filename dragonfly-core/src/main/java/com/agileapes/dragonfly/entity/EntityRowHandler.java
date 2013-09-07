package com.agileapes.dragonfly.entity;

import com.agileapes.dragonfly.metadata.TableMetadata;

import java.sql.ResultSet;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:14)
 */
public interface EntityRowHandler {

    <E> Map<String, Object> handleRow(ResultSet resultSet);

}
