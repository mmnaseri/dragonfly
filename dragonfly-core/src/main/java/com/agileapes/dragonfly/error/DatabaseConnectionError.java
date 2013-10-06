package com.agileapes.dragonfly.error;

import java.sql.SQLException;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/21, 14:09)
 */
public class DatabaseConnectionError extends DatabaseError {

    public DatabaseConnectionError(SQLException cause) {
        super("Failed to obtain database connection", cause);
    }

}
