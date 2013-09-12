package com.agileapes.dragonfly.model;

import com.agileapes.dragonfly.metadata.TableMetadata;

import java.util.Collection;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 13:49)
 */
public class MetadataGenerationModel {

    private Collection<TableMetadata<?>> tables;
    private ColumnIndexMethod index = new ColumnIndexMethod();
    private EscapeStringMethod escape = new EscapeStringMethod();

    public Collection<TableMetadata<?>> getTables() {
        return tables;
    }

    public void setTables(Collection<TableMetadata<?>> tables) {
        this.tables = tables;
    }

    public ColumnIndexMethod getIndex() {
        return index;
    }

    public EscapeStringMethod getEscape() {
        return escape;
    }
}
