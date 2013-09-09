package com.agileapes.dragonfly.security.impl;

import com.agileapes.dragonfly.security.Subject;

import java.lang.reflect.Method;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 17:01)
 */
public class MethodSubject implements Subject {

    private final Method method;

    public MethodSubject(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

}
