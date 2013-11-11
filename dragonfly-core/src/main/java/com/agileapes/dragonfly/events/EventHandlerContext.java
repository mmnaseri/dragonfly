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

package com.agileapes.dragonfly.events;

/**
 * The event handler context allows for listeners to be introduced so that events fired
 * through the event handler context are attributed to those listeners.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 1:46)
 */
public interface EventHandlerContext {

    /**
     * Adds a new event handler to the context
     * @param eventHandler    the event handler client to the events of the context
     */
    void addHandler(DataAccessEventHandler eventHandler);

}
