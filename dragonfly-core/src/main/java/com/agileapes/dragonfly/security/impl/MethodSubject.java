package com.agileapes.dragonfly.security.impl;

import com.agileapes.couteau.enhancer.api.MethodDescriptor;
import com.agileapes.dragonfly.security.Subject;

import java.lang.reflect.Method;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 17:01)
 */
public class MethodSubject implements Subject {

    private final MethodDescriptor method;

    public MethodSubject(MethodDescriptor method) {
        this.method = method;
    }

    public MethodDescriptor getMethod() {
        return method;
    }

}
