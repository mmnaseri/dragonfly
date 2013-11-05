package com.agileapes.dragonfly.security.impl;

import com.agileapes.dragonfly.security.Subject;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 2:41)
 */
public class StoredProcedureSubject implements Subject {

    private final Class<?> entityType;
    private final String procedureName;
    final Object[] parameters;

    public StoredProcedureSubject(Class<?> entityType, final String procedureName, Object[] parameters) {
        this.entityType = entityType;
        this.procedureName = procedureName;
        this.parameters = parameters;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public Object[] getParameters() {
        return parameters;
    }

}
