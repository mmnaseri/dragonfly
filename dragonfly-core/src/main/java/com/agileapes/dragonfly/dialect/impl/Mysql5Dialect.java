package com.agileapes.dragonfly.dialect.impl;

import com.agileapes.dragonfly.dialect.DatabaseDialect;
import com.agileapes.dragonfly.error.DatabaseMetadataAccessError;
import com.agileapes.dragonfly.error.UnknownColumnTypeError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/4, 14:15)
 */
public class Mysql5Dialect implements DatabaseDialect {

    @Override
    public Character getIdentifierEscapeCharacter() {
        return '`';
    }

    @Override
    public Character getSchemaSeparator() {
        return '.';
    }

    @Override
    public Character getStringEscapeCharacter() {
        return '\\';
    }

    @Override
    public String getType(ColumnMetadata columnMetadata) {
        final int columnType = columnMetadata.getType();
        if (columnType == Types.BIT) {
            return "BIT (" + (columnMetadata.getLength() <= 0 ? 1 : columnMetadata.getLength()) + ")";
        } else if (columnType == Types.TINYINT) {
            return "TINYINT";
        } else if (columnType == Types.SMALLINT) {
            return "SMALLINT";
        } else if (columnType == Types.INTEGER) {
            return "INTEGER";
        } else if (columnType == Types.BIGINT) {
            return "BIGINT";
        } else if (columnType == Types.FLOAT) {
            return "FLOAT";
        } else if (columnType == Types.REAL) {
            return "REAL";
        } else if (columnType == Types.DOUBLE) {
            return "DOUBLE";
        } else if (columnType == Types.NUMERIC) {
            return "NUMERIC (" + (columnMetadata.getPrecision() <= 0 ? "M" : columnMetadata.getPrecision()) +
                        (columnMetadata.getScale() <= 0 ? "" : "," + columnMetadata.getScale()) + ")";
        } else if (columnType == Types.DECIMAL) {
            return "DECIMAL (" + (columnMetadata.getPrecision() <= 0 ? "M" : columnMetadata.getPrecision()) +
                        (columnMetadata.getScale() <= 0 ? "" : "," + columnMetadata.getScale()) + ")";
        } else if (columnType == Types.CHAR) {
            return "CHAR (" + (columnMetadata.getLength() <= 0 ? 1 : (columnMetadata.getLength() > 255 ? 255 : columnMetadata.getLength())) + ")";
        } else if (columnType == Types.VARCHAR) {
            return "VARCHAR (" + (columnMetadata.getLength() <= 0 ? 1 : (columnMetadata.getLength() > 65535 ? 65535 : columnMetadata.getLength())) + ")";
        } else if (columnType == Types.LONGNVARCHAR) {
            return "LONGVARCHAR (" + (columnMetadata.getLength() <= 0 ? 1 : columnMetadata.getLength()) + ")";
        } else if (columnType == Types.DATE) {
            return "DATE";
        } else if (columnType == Types.TIME) {
            return "TIME";
        } else if (columnType == Types.TIMESTAMP) {
            return "TIMESTAMP";
        } else if (columnType == Types.BINARY) {
            return "BINARY (" + (columnMetadata.getLength() <= 0 ? 1 : (columnMetadata.getLength() > 255 ? 255 : columnMetadata.getLength())) + ")";
        } else if (columnType == Types.VARBINARY) {
            return "VARBINARY (" + (columnMetadata.getLength() <= 0 ? 1 : (columnMetadata.getLength() > 65535 ? 65535 : columnMetadata.getLength())) + ")";
        } else if (columnType == Types.LONGVARBINARY) {
            return "LONGVARBINARY (" + (columnMetadata.getLength() <= 0 ? 1 : (columnMetadata.getLength() > 65535 ? 65535 : columnMetadata.getLength())) + ")";
        } else if (columnType == Types.BOOLEAN) {
            return "BOOLEAN";
        }
        throw new UnknownColumnTypeError(columnType);
    }

    @Override
    public boolean accepts(DatabaseMetaData databaseMetaData) {
        try {
            return databaseMetaData.getDatabaseProductName().toLowerCase().matches("mysql") && databaseMetaData.getDatabaseMajorVersion() == 5;
        } catch (SQLException e) {
            throw new DatabaseMetadataAccessError(e);
        }
    }

}
