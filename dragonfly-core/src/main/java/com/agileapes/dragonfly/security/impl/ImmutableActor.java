package com.agileapes.dragonfly.security.impl;

import com.agileapes.dragonfly.security.Actor;

import java.lang.reflect.Method;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 17:04)
 */
public class ImmutableActor implements Actor {

    private final Class<?> actorClass;
    private final Method method;

    public ImmutableActor(Class<?> actorClass, Method method) {
        this.actorClass = actorClass;
        this.method = method;
    }

    @Override
    public Class<?> getActorClass() {
        return actorClass;
    }

    @Override
    public Method getMethod() {
        return method;
    }
}
