package com.agileapes.dragonfly.security.impl;

import com.agileapes.dragonfly.security.AccessDeniedHandler;
import com.agileapes.dragonfly.security.Actor;
import com.agileapes.dragonfly.security.Subject;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 23:39)
 */
public class FatalAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(String policy, Actor actor, Subject subject) {
        if (policy == null) {
            throw new SecurityException("Access to " + subject + " was denied to " + actor + " because it was not given an explicit pass");
        }
        throw new SecurityException("Access to " + subject + " was denied to " + actor + " due to " + policy);
    }

}
