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
 * This class holds enums that help with classification of the different statements supported by the core
 * framework. Note that declaring a statement type here does not mean that it will be supported by the
 * dialect being used.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 0:53)
 */
public class Statements {

    private Statements() {}

    /**
     * This enum holds all the different data definition statements recognized by the framework.
     * These are statements supported by {@link com.agileapes.dragonfly.data.DataStructureHandler}
     */
    public static enum Definition {
        /**
         * Statement to create a table
         */
        CREATE_TABLE,
        /**
         * Drop a table
         */
        DROP_TABLE,
        /**
         * Issue commands for creation of a primary key index
         */
        CREATE_PRIMARY_KEY,
        /**
         * Issue commands for deletion of a primary key index
         */
        DROP_PRIMARY_KEY,
        /**
         * Command for creation of a foreign key index
         */
        CREATE_FOREIGN_KEY,
        /**
         * Command for deletion of a foreign key index
         */
        DROP_FOREIGN_KEY,
        /**
         * Create a unique key constraint
         */
        CREATE_UNIQUE_CONSTRAINT,
        /**
         * Drop a unique key constraint
         */
        DROP_UNIQUE_CONSTRAINT,
        /**
         * Create a database managed sequence
         */
        CREATE_SEQUENCE,
        /**
         * Drop a database managed sequence
         */
        DROP_SEQUENCE,
        /**
         * Bind a sequence to its value column
         */
        BIND_SEQUENCE,
        /**
         * Unbind a sequence from its value column
         */
        UNBIND_SEQUENCE
    }

    /**
     * This enum allows for declaration of all recognized data manipulation statements.
     * There are statements supported by {@link com.agileapes.dragonfly.data.DataAccess}
     */
    public static enum Manipulation {
        /**
         * Deletes all entities of a given type
         */
        DELETE_ALL,
        /**
         * Deletes only one specific item, ideally specified through its primary key
         */
        DELETE_ONE,
        /**
         * Deletes all elements like the given sample
         */
        DELETE_LIKE,
        /**
         * Deletes all cascading entities when deleting all items of a given type
         */
        DELETE_DEPENDENCIES,
        /**
         * Delete all cascaded dependent items when deleting all items of a given type
         */
        DELETE_DEPENDENTS,
        /**
         * Finds all items of a given type
         */
        FIND_ALL,
        /**
         * Finds the one item that has the given primary identifier
         */
        FIND_ONE,
        /**
         * Finds all items matching the sample
         */
        FIND_LIKE,
        /**
         * Counts all items of a given type
         */
        COUNT_ALL,
        /**
         * Counts the item with the key (either {@code 0} or {@code 1}).
         */
        COUNT_ONE,
        /**
         * Counts the items matching the given sample
         */
        COUNT_LIKE,
        /**
         * Inserts a new row into the database
         */
        INSERT,
        /**
         * Updates some rows in the database
         */
        UPDATE,
        /**
         * Truncates the table for a given entity type
         */
        TRUNCATE,
        /**
         * Loads the foreign side of a many-to-many relation
         */
        LOAD_MANY_TO_MANY,
        /**
         * Calls to a given procedure
         */
        CALL
    }

}
