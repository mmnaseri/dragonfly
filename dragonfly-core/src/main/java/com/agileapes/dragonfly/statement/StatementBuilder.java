package com.agileapes.dragonfly.statement;

import com.agileapes.dragonfly.metadata.ConstraintMetadata;
import com.agileapes.dragonfly.metadata.Metadata;
import com.agileapes.dragonfly.metadata.TableMetadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/1, 1:21)
 */
public interface StatementBuilder {

    Statement getStatement(TableMetadata<?> tableMetadata, Metadata metadata);

    Statement getStatement(TableMetadata<?> tableMetadata);

}
