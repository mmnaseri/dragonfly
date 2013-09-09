package com.agileapes.dragonfly.security;

import java.lang.reflect.Method;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 16:49)
 */
public interface Actor {

    Class<?> getActorClass();

    Method getMethod();

}
