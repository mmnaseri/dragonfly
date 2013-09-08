package com.agileapes.dragonfly.dialect;

import com.agileapes.couteau.basics.api.Filter;
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
public interface DatabaseDialect extends Filter<DatabaseMetaData> {

    Character getIdentifierEscapeCharacter();

    Character getSchemaSeparator();

    Character getStringEscapeCharacter();

    String getType(ColumnMetadata columnMetadata);

    StatementBuilderContext getStatementBuilderContext();

    Serializable retrieveKey(ResultSet generatedKeys, TableMetadata<?> tableMetadata) throws SQLException;

    String getName();

    int getDefaultPort();

    <E> boolean hasTable(DatabaseMetaData databaseMetadata, TableMetadata<E> tableMetadata);

}
