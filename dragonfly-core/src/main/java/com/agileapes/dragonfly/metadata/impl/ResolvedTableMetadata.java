package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.dragonfly.error.MetadataCollectionError;
import com.agileapes.dragonfly.error.NoPrimaryKeyDefinedError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.ConstraintMetadata;
import com.agileapes.dragonfly.metadata.NamedQueryMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.tools.ConstraintTypeFilter;

import java.util.Collection;
import java.util.Collections;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:35)
 */
public class ResolvedTableMetadata<E> extends AbstractTableMetadata<E> {

    private final String name;
    private final String schema;
    private final Collection<ConstraintMetadata> constraints;
    private final Collection<ColumnMetadata> columns;
    private PrimaryKeyConstraintMetadata primaryKey = null;
    private final Collection<NamedQueryMetadata> namedQueries;

    public ResolvedTableMetadata(Class<E> entityType, String schema, String name, Collection<ConstraintMetadata> constraints, Collection<ColumnMetadata> columns, Collection<NamedQueryMetadata> namedQueries) {
        super(entityType);
        this.schema = schema;
        this.name = name;
        this.constraints = constraints;
        this.columns = columns;
        this.namedQueries = namedQueries;
        for (ColumnMetadata column : columns) {
            if (column instanceof ResolvedColumnMetadata) {
                ResolvedColumnMetadata metadata = (ResolvedColumnMetadata) column;
                metadata.setTable(this);
            }
        }
        for (ConstraintMetadata constraint : constraints) {
            ((AbstractConstraintMetadata) constraint).setTable(this);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public Collection<ConstraintMetadata> getConstraints() {
        return Collections.unmodifiableCollection(constraints);
    }

    @Override
    public Collection<ColumnMetadata> getColumns() {
        return Collections.unmodifiableCollection(columns);
    }

    @Override
    public boolean hasPrimaryKey() {
        prepareKey();
        return primaryKey != null;
    }

    @Override
    public Collection<NamedQueryMetadata> getNamedQueries() {
        return namedQueries;
    }

    @Override
    public PrimaryKeyConstraintMetadata getPrimaryKey() {
        if (!hasPrimaryKey()) {
            throw new MetadataCollectionError("Cannot return table primary key", new NoPrimaryKeyDefinedError(this));
        }
        return primaryKey;
    }

    private void prepareKey() {
        if (primaryKey == null) {
            primaryKey = (PrimaryKeyConstraintMetadata) with(constraints).keep(new ConstraintTypeFilter(PrimaryKeyConstraintMetadata.class)).first();
        }
    }

    @Override
    public <C extends ConstraintMetadata> Collection<C> getConstraints(Class<C> constraintType) {
        //noinspection unchecked
        return (Collection<C>) with(getConstraints()).keep(new ConstraintTypeFilter(constraintType)).list();
    }

    @Override
    public Collection<TableMetadata<?>> getForeignReferences() {
        return with(getConstraints(ForeignKeyConstraintMetadata.class)).transform(new Transformer<ConstraintMetadata, ForeignKeyConstraintMetadata>() {
            @Override
            public ForeignKeyConstraintMetadata map(ConstraintMetadata constraintMetadata) {
                return (ForeignKeyConstraintMetadata) constraintMetadata;
            }
        }).transform(new Transformer<ForeignKeyConstraintMetadata, TableMetadata<?>>() {
            @Override
            public TableMetadata<?> map(ForeignKeyConstraintMetadata foreignKeyConstraintMetadata) {
                return foreignKeyConstraintMetadata.getColumn().getForeignReference().getTable();
            }
        }).list();
    }

}
