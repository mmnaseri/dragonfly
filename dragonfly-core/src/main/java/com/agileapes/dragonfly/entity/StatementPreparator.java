package com.agileapes.dragonfly.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 17:00)
 */
public interface StatementPreparator {

    PreparedStatement prepare(Connection connection, String sql, Map<String, Object> value);

}
