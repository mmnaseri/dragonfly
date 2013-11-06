package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/11/6, 15:44)
 */
public class DatabaseDriverNotFoundError extends DatabaseError {

    public DatabaseDriverNotFoundError(String className) {
        super("Driver class for the database was not found: " + className);
    }

}
