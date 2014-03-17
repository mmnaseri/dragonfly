package com.agileapes.dragonfly.analysis.impl;

import com.agileapes.dragonfly.analysis.IssueTarget;

import java.lang.reflect.Method;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 15:33)
 */
public class MethodIssueTarget implements IssueTarget<Method> {

    private final Method method;

    public MethodIssueTarget(Method method) {
        this.method = method;
    }

    @Override
    public Method getTarget() {
        return method;
    }

    @Override
    public String toString() {
        return "method declaration '" + method.toString() + "'";
    }
}
