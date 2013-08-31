package com.agileapes.dragonfly.metadata.impl;

import com.agileapes.couteau.basics.api.Transformer;
import com.agileapes.dragonfly.error.MetadataCollectionError;
import com.agileapes.dragonfly.error.NoPrimaryKeyDefinedError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.ConstraintMetadata;
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
    private final PrimaryKeyConstraintMetadata primaryKey;

    public ResolvedTableMetadata(Class<E> entityType, String schema, String name, Collection<ConstraintMetadata> constraints, Collection<ColumnMetadata> columns) {
        super(entityType);
        this.schema = schema;
        this.name = name;
        this.constraints = constraints;
        this.columns = columns;
        for (ColumnMetadata column : columns) {
            if (column instanceof ResolvedColumnMetadata) {
                ResolvedColumnMetadata metadata = (ResolvedColumnMetadata) column;
                metadata.setTable(this);
            }
        }
        final Collection<PrimaryKeyConstraintMetadata> data = getConstraints(PrimaryKeyConstraintMetadata.class);
        this.primaryKey = data.isEmpty() ? null : data.iterator().next();
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
    public PrimaryKeyConstraintMetadata getPrimaryKey() {
        if (primaryKey == null) {
            throw new MetadataCollectionError("Cannot return table primary key", new NoPrimaryKeyDefinedError(this));
        }
        return primaryKey;
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
                return foreignKeyConstraintMetadata.getTable();
            }
        }).list();
    }

}
