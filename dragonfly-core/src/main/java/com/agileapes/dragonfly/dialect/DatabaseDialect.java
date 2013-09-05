package com.agileapes.dragonfly.dialect;

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.statement.StatementBuilderContext;

import java.sql.DatabaseMetaData;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/1, 1:33)
 */
public interface DatabaseDialect {

    Character getIdentifierEscapeCharacter();

    Character getSchemaSeparator();

    Character getStringEscapeCharacter();

    String getType(ColumnMetadata columnMetadata);

    boolean accepts(DatabaseMetaData databaseMetaData);

    StatementBuilderContext getStatementBuilderContext();

}
