/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.data.impl;

import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.metadata.impl.ImmutableNamedQueryMetadata;
import com.agileapes.dragonfly.metadata.impl.ResolvedColumnMetadata;
import com.agileapes.dragonfly.metadata.impl.ResolvedTableMetadata;
import com.agileapes.dragonfly.metadata.impl.UniqueConstraintMetadata;

import java.sql.Types;
import java.util.*;

/**
 * This is an entity designated for generation of keys that are to be automatically maintained
 * through the {@link ValueGenerationType#TABLE} strategy.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 17:58)
 */
public class TableKeyGeneratorEntity {

    private String name;
    private Long value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public static TableMetadata<TableKeyGeneratorEntity> getTableMetadata(String schema) {
        final HashSet<ConstraintMetadata> constraints = new HashSet<ConstraintMetadata>();
        final HashSet<ColumnMetadata> columns = new HashSet<ColumnMetadata>();
        final ResolvedColumnMetadata nameColumn = new ResolvedColumnMetadata(null, TableKeyGeneratorEntity.class, "name", Types.VARCHAR, "name", String.class, false, 256, 0, 0);
        columns.add(nameColumn);
        columns.add(new ResolvedColumnMetadata(null, TableKeyGeneratorEntity.class, "value", Types.BIGINT, "value", Long.class, false, 0, 0, 0));
        final List<NamedQueryMetadata> namedQueries = new ArrayList<NamedQueryMetadata>();
        namedQueries.add(new ImmutableNamedQueryMetadata("increment", "UPDATE ${qualify(table)} SET ${escape('value')} = ${escape('value')} + 1 WHERE ${escape('name')} = ${value.name}"));
        final ResolvedTableMetadata<TableKeyGeneratorEntity> tableMetadata = new ResolvedTableMetadata<TableKeyGeneratorEntity>(TableKeyGeneratorEntity.class, schema, "dragonfly_sequences", constraints, columns, namedQueries, Collections.<SequenceMetadata>emptyList(), Collections.<StoredProcedureMetadata>emptyList(), Collections.<ReferenceMetadata<TableKeyGeneratorEntity, ?>>emptyList(), null, null);
        constraints.add(new UniqueConstraintMetadata(tableMetadata, Arrays.<ColumnMetadata>asList(nameColumn)));
        return tableMetadata;
    }

}
