package com.agileapes.dragonfly.security;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 23:39)
 */
public interface AccessDeniedHandler {

    void handle(String policy, Actor actor, Subject subject);

}
