package com.agileapes.dragonfly.data.impl;

import com.agileapes.dragonfly.statement.Statements;

/**
 * This class is designed to hold results for a given operation for which results must be cached
 * for later reference.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/5, 9:25)
 */
public class LocalOperationResult {

    private final Class<?> entityType;
    private final Statements.Manipulation statement;
    private final Object request;
    private Object result;

    public LocalOperationResult(Class<?> entityType, Statements.Manipulation statement, Object request) {
        this.entityType = entityType;
        this.statement = statement;
        this.request = request;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalOperationResult that = (LocalOperationResult) o;
        return !(entityType != null ? !entityType.equals(that.entityType) : that.entityType != null) && !(request != null ? !request.equals(that.request) : that.request != null) && statement == that.statement;
    }

    @Override
    public int hashCode() {
        int result = entityType != null ? entityType.hashCode() : 0;
        result = 31 * result + (statement != null ? statement.hashCode() : 0);
        result = 31 * result + (request != null ? request.hashCode() : 0);
        return result;
    }
}
