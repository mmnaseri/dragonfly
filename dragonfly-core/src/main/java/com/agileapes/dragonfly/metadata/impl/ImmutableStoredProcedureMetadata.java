package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.dragonfly.metadata.ParameterMetadata;
import com.agileapes.dragonfly.metadata.StoredProcedureMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 0:51)
 */
public class ImmutableStoredProcedureMetadata implements StoredProcedureMetadata {

    private final String name;
    private final Class<?> resultType;
    private final List<ParameterMetadata> parameters;
    private TableMetadata<?> tableMetadata;

    public ImmutableStoredProcedureMetadata(String name, Class<?> resultType, List<ParameterMetadata> parameters) {
        this.name = name;
        this.resultType = resultType;
        this.parameters = parameters;
    }

    @Override
    public TableMetadata<?> getTable() {
        return tableMetadata;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getResultType() {
        return resultType;
    }

    @Override
    public List<ParameterMetadata> getParameters() {
        return parameters;
    }

    public void setTable(TableMetadata<?> tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

}
