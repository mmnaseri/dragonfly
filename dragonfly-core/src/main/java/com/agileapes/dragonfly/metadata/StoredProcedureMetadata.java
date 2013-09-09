package com.agileapes.dragonfly.metadata;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 0:44)
 */
public interface StoredProcedureMetadata extends Metadata {

    TableMetadata<?> getTable();

    String getName();

    Class<?> getResultType();

    List<ParameterMetadata> getParameters();

}
