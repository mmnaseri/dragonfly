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

package com.agileapes.dragonfly.security;

/**
 * This interface allows for a flexible handling of all denials of access, as raised by the
 * {@link com.agileapes.dragonfly.security.DataSecurityManager}.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 23:39)
 */
public interface AccessDeniedHandler {

    /**
     * This method is called by the security manager to allow for handling of data access denials
     * @param policy     the name of the policy leading to such a denial of access
     * @param actor      the actor failing to secure access
     * @param subject    the subject to which the actor was trying to gain access
     */
    void handle(String policy, Actor actor, Subject subject);

}
