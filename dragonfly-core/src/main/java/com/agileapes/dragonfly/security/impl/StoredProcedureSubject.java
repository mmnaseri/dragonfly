package com.agileapes.dragonfly.security.impl;

import com.agileapes.dragonfly.metadata.StoredProcedureMetadata;
import com.agileapes.dragonfly.security.Subject;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 2:41)
 */
public class StoredProcedureSubject implements Subject {

    final StoredProcedureMetadata procedureMetadata;
    final Object[] parameters;

    public StoredProcedureSubject(StoredProcedureMetadata procedureMetadata, Object[] parameters) {
        this.procedureMetadata = procedureMetadata;
        this.parameters = parameters;
    }

    public StoredProcedureMetadata getProcedureMetadata() {
        return procedureMetadata;
    }

    public Object[] getParameters() {
        return parameters;
    }

}
