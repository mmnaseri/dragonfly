package com.agileapes.dragonfly.statement;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 0:53)
 */
public class Statements {

    private Statements() {}

    public static enum Definition {
        CREATE_TABLE,
        DROP_TABLE,
        CREATE_PRIMARY_KEY,
        DROP_PRIMARY_KEY,
        CREATE_FOREIGN_KEY,
        DROP_FOREIGN_KEY,
        CREATE_UNIQUE_CONSTRAINT,
        DROP_UNIQUE_CONSTRAINT,
        CREATE_SEQUENCE,
        DROP_SEQUENCE,
        BIND_SEQUENCE,
        UNBIND_SEQUENCE
    }

    public static enum Manipulation {
        DELETE_ALL,
        DELETE_ONE,
        DELETE_LIKE,
        FIND_ALL,
        FIND_ONE,
        FIND_LIKE,
        INSERT,
        UPDATE,
        TRUNCATE
    }

}
