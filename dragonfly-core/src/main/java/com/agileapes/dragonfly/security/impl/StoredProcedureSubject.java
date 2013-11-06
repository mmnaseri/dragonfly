/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

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
