package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/4, 14:15)
 */
public class UnknownColumnTypeError extends DatabaseError {

    public UnknownColumnTypeError(int columnType) {
        super("Unknown column constant type: " + columnType);
    }

}
