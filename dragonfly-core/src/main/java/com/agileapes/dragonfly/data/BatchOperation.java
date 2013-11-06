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

package com.agileapes.dragonfly.data;

/**
 * This interface allows for designation of batch operation callbacks. This allows for a much simpler
 * way of writing batch operations. This way, you can write your batch operations unobtrusively
 * as if you were simply interacting with the normal data access operation interface, and the executing
 * data access will support the batch execution seamlessly.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/25, 0:16)
 */
public interface BatchOperation {

    /**
     * This method is called when the batch operation must be started. After the execution
     * of this method is done, the batch context is committed to the database.
     * @param dataAccess    the data access on which the batch operations will be performed.
     */
    void execute(DataAccess dataAccess);

}
