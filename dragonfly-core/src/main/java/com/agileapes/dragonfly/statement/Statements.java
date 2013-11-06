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
        DELETE_DEPENDENCIES,
        DELETE_DEPENDENTS,
        FIND_ALL,
        FIND_ONE,
        FIND_LIKE,
        COUNT_ALL,
        COUNT_ONE,
        COUNT_LIKE,
        INSERT,
        UPDATE,
        TRUNCATE,
        LOAD_MANY_TO_MANY,
        CALL
    }

}
