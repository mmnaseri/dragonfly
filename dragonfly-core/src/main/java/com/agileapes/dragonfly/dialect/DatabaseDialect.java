package com.agileapes.dragonfly.dialect;

import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.statement.StatementBuilderContext;

import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    Serializable retrieveKey(ResultSet generatedKeys, TableMetadata<?> tableMetadata) throws SQLException;

    String getName();

    int getDefaultPort();

}
