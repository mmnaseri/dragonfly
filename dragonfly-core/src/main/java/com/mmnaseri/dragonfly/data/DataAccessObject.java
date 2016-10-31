/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mmnaseri.dragonfly.data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>This interface allows for a domain-driven view at entities. All entities in this
 * framework can be cast to the relevant type of {@link DataAccessObject} to enable
 * the user to perform the most basic tasks of working with a DAO without having to use
 * the more complex {@link DataAccess} interface's functionalities.</p>
 *
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/5, 19:25)
 */
public interface DataAccessObject<E, K extends Serializable> {

    /**
     * Refreshes the entity from the database, changing the values of its properties to
     * reflect the most up-to-date values in the database.
     */
    void load();

    /**
     * Saves the entity to database. This is done either by inserting a new entry in the
     * database, or by updating an existing one.
     * @see DataAccess#save(Object)
     */
    void save();

    /**
     * Deletes the given entity from database. After calling this method, the DAO cannot
     * be used, as it is assumed to have been completely unlinked from the database, thus
     * rendering it useless from a practical point of view.
     * @see DataAccess#delete(Object)
     */
    void delete();

    /**
     * Finds all items like this one
     * @return a list of items in the database that resemble the entity wrapped by the data access object at hand
     */
    List<E> findLike();

    /**
     * Runs the query specified, while using this item as a sample for value injection
     * @param queryName    the name of the query
     * @return the list of matching items
     */
    List<E> query(String queryName);

    /**
     * Runs the update query specified, while using this item as a sample for value injection
     * @param queryName    the name of the query
     * @return the number of affected items
     */
    int update(String queryName);

}
