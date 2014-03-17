package com.agileapes.dragonfly.analysis.impl;

import com.agileapes.dragonfly.analysis.IssueTarget;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/3/17 AD, 15:27)
 */
public class EntityIssueTarget implements IssueTarget<Class<?>> {

    private final Class<?> entityType;

    public EntityIssueTarget(Class<?> entityType) {
        this.entityType = entityType;
    }

    @Override
    public Class<?> getTarget() {
        return entityType;
    }

    @Override
    public String toString() {
        return "entity '" + entityType.getCanonicalName() + "'";
    }
}
