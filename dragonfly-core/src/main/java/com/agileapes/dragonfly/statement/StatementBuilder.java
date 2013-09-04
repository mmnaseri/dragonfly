package com.agileapes.dragonfly.statement;

import com.agileapes.dragonfly.metadata.TableMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/1, 1:21)
 */
public interface StatementBuilder {

    Statement getStatement(TableMetadata<?> tableMetadata);

}
