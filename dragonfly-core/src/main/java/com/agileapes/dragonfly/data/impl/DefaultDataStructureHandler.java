/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.agileapes.dragonfly.data.impl;

import com.mmnaseri.couteau.basics.api.Filter;
import com.mmnaseri.couteau.basics.api.Processor;
import com.agileapes.dragonfly.annotations.Ignored;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.data.DataStructureHandler;
import com.agileapes.dragonfly.error.UnsuccessfulOperationError;
import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.metadata.impl.DefaultTableMetadataContext;
import com.agileapes.dragonfly.metadata.impl.ForeignKeyConstraintMetadata;
import com.agileapes.dragonfly.metadata.impl.UniqueConstraintMetadata;
import com.agileapes.dragonfly.statement.Statement;
import com.agileapes.dragonfly.statement.Statements;
import com.agileapes.dragonfly.tools.SequenceColumnFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;
import static com.agileapes.dragonfly.statement.Statements.Definition.*;

/**
 * This class helps with managing of data structures for the database. It assumes a relational
 * setting for the target data source.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 14:31)
 */
public class DefaultDataStructureHandler implements DataStructureHandler {

    private static final Log log = LogFactory.getLog(DataStructureHandler.class);
    private final DataAccessSession session;
    private final TableMetadataRegistry auxiliaryRegistry;

    public DefaultDataStructureHandler(DataAccessSession session, TableMetadataRegistry auxiliaryRegistry) {
        this.session = session;
        this.auxiliaryRegistry = auxiliaryRegistry;
    }

    private <E> void executeStatement(TableMetadata<E> tableMetadata, Statements.Definition statementType, Metadata metadata) {
        try {
            final Statement statement = session.getDatabaseDialect().getStatementBuilderContext().getDefinitionStatementBuilder(statementType).getStatement(tableMetadata, metadata);
            if (statement.getSql().trim().isEmpty()) {
                return;
            }
            final Connection connection = session.getConnection();
            final PreparedStatement preparedStatement = statement.prepare(connection);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to execute statement: " + statementType, e);
        }
    }

    @Override
    public <E> void defineTable(Class<E> entityType) {
        defineTable(session.getTableMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void definePrimaryKey(Class<E> entityType) {
        definePrimaryKey(session.getTableMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void defineSequences(final Class<E> entityType) {
        defineSequences(session.getTableMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void defineForeignKeys(final Class<E> entityType) {
        defineForeignKeys(session.getTableMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void defineUniqueConstraints(final Class<E> entityType) {
        defineUniqueConstraints(session.getTableMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void removeTable(Class<E> entityType) {
        removeTable(session.getTableMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void removePrimaryKeys(Class<E> entityType) {
        removePrimaryKeys(session.getTableMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void removeSequences(final Class<E> entityType) {
        removeSequences(session.getTableMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void removeForeignKeys(final Class<E> entityType) {
        removeForeignKeys(session.getTableMetadataRegistry().getTableMetadata(entityType));
    }

    @Override

    public <E> void removeUniqueConstraints(final Class<E> entityType) {
        removeUniqueConstraints(session.getTableMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void bindSequences(final Class<E> entityType) {
        bindSequences(session.getTableMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void unbindSequences(final Class<E> entityType) {
        unbindSequences(session.getTableMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void defineTable(TableMetadata<E> tableMetadata) {
        log.info("Defining table for type " + tableMetadata.getName());
        executeStatement(tableMetadata, CREATE_TABLE, null);
    }

    @Override
    public <E> void definePrimaryKey(TableMetadata<E> tableMetadata) {
        log.info("Defining primary key for " + tableMetadata.getName());
        if (tableMetadata.hasPrimaryKey()) {
            executeStatement(tableMetadata, CREATE_PRIMARY_KEY, null);
        }
    }

    @Override
    public <E> void defineSequences(final TableMetadata<E> tableMetadata) {
        with(tableMetadata.getSequences()).each(new Processor<SequenceMetadata>() {
            @Override
            public void process(SequenceMetadata sequenceMetadata) {
                log.info("Defining sequence (" + sequenceMetadata.getName() + ") on " + tableMetadata.getName());
                executeStatement(tableMetadata, CREATE_SEQUENCE, sequenceMetadata);
            }
        });
    }

    @Override
    public <E> void defineForeignKeys(final TableMetadata<E> tableMetadata) {
        with(tableMetadata.getConstraints(ForeignKeyConstraintMetadata.class)).each(new Processor<ForeignKeyConstraintMetadata>() {
            @Override
            public void process(ForeignKeyConstraintMetadata foreignKeyConstraintMetadata) {
                log.info("Defining foreign key constraint (" + foreignKeyConstraintMetadata.getName() + ") for " + tableMetadata.getName());
                executeStatement(tableMetadata, CREATE_FOREIGN_KEY, foreignKeyConstraintMetadata);
            }
        });
    }

    @Override
    public <E> void defineUniqueConstraints(final TableMetadata<E> tableMetadata) {
        with(tableMetadata.getConstraints(UniqueConstraintMetadata.class)).each(new Processor<UniqueConstraintMetadata>() {
            @Override
            public void process(UniqueConstraintMetadata uniqueConstraintMetadata) {
                log.info("Defining unique constraint (" + uniqueConstraintMetadata.getName() + ") on " + tableMetadata.getName());
                executeStatement(tableMetadata, CREATE_UNIQUE_CONSTRAINT, uniqueConstraintMetadata);
            }
        });
        with(tableMetadata.getColumns()).forThose(
                new Filter<ColumnMetadata>() {
                    @Override
                    public boolean accepts(ColumnMetadata item) {
                        return ValueGenerationType.TABLE.equals(item.getGenerationType());
                    }
                },
                new Processor<ColumnMetadata>() {
                    @Override
                    public void process(ColumnMetadata input) {
                        executeStatement(tableMetadata, CREATE_UNIQUE_CONSTRAINT, new UniqueConstraintMetadata(tableMetadata, Arrays.asList(input)));
                    }
                }
        );
    }

    @Override
    public <E> void removeTable(TableMetadata<E> tableMetadata) {
        log.warn("Removing table " + tableMetadata.getName());
        executeStatement(tableMetadata, DROP_TABLE, null);
    }

    @Override
    public <E> void removePrimaryKeys(TableMetadata<E> tableMetadata) {
        log.info("Removing primary key definition " + tableMetadata.getName());
        executeStatement(tableMetadata, DROP_PRIMARY_KEY, null);
    }

    @Override
    public <E> void removeSequences(final TableMetadata<E> tableMetadata) {
        with(tableMetadata.getSequences()).each(new Processor<SequenceMetadata>() {
            @Override
            public void process(SequenceMetadata sequenceMetadata) {
                log.info("Removing sequence metadata " + sequenceMetadata.getName() + " from " + tableMetadata.getName());
                executeStatement(tableMetadata, DROP_SEQUENCE, sequenceMetadata);
            }
        });
    }

    @Override
    public <E> void removeForeignKeys(final TableMetadata<E> tableMetadata) {
        with(tableMetadata.getConstraints(ForeignKeyConstraintMetadata.class)).each(new Processor<ForeignKeyConstraintMetadata>() {
            @Override
            public void process(ForeignKeyConstraintMetadata foreignKeyConstraintMetadata) {
                log.info("Removing foreign key metadata " + foreignKeyConstraintMetadata.getName() + " from " + tableMetadata.getName());
                executeStatement(tableMetadata, DROP_FOREIGN_KEY, foreignKeyConstraintMetadata);
            }
        });
    }

    @Override
    public <E> void removeUniqueConstraints(final TableMetadata<E> tableMetadata) {
        with(tableMetadata.getConstraints(UniqueConstraintMetadata.class)).each(new Processor<UniqueConstraintMetadata>() {
            @Override
            public void process(UniqueConstraintMetadata uniqueConstraintMetadata) {
                log.info("Removing unique constraint metadata " + uniqueConstraintMetadata.getName() + " from " + tableMetadata.getName());
                executeStatement(tableMetadata, DROP_UNIQUE_CONSTRAINT, uniqueConstraintMetadata);
            }
        });
    }

    @Override
    public <E> void bindSequences(final TableMetadata<E> tableMetadata) {
        with(tableMetadata.getColumns()).keep(new SequenceColumnFilter()).each(new Processor<ColumnMetadata>() {
            @Override
            public void process(ColumnMetadata columnMetadata) {
                log.info("Binding sequences to " + columnMetadata.getName() + " on " + tableMetadata.getName());
                executeStatement(tableMetadata, BIND_SEQUENCE, columnMetadata);
            }
        });
    }

    @Override
    public <E> void unbindSequences(final TableMetadata<E> tableMetadata) {
        with(tableMetadata.getColumns()).keep(new SequenceColumnFilter()).each(new Processor<ColumnMetadata>() {
            @Override
            public void process(ColumnMetadata columnMetadata) {
                log.info("Unbinding sequences from " + columnMetadata.getName() + " on " + tableMetadata.getName());
                executeStatement(tableMetadata, UNBIND_SEQUENCE, columnMetadata);
            }
        });
    }

    @Override
    public <E> boolean isDefined(Class<E> entityType) {
        return isDefined(session.getTableMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> boolean isDefined(TableMetadata<E> tableMetadata) {
        try {
            final Connection connection = session.getConnection();
            final boolean result = session.getDatabaseDialect().hasTable(connection.getMetaData(), tableMetadata);
            connection.close();
            return result;
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to check definition for table", e);
        }
    }

    @Override
    public synchronized void initialize() {
        log.info("Initializing entity table structures");
        initialize(session.getTableMetadataRegistry().getTables());
        if (auxiliaryRegistry != null && auxiliaryRegistry instanceof DefaultTableMetadataContext) {
            DefaultTableMetadataContext context = (DefaultTableMetadataContext) session.getTableMetadataRegistry();
            final Set<TableMetadata<?>> tables = context.getVirtualTables();
            initialize(tables);
        }
    }

    @Override
    public void initialize(Collection<TableMetadata<?>> tables) {
        //noinspection unchecked
        tables = with(tables)
                //ignore entities which are marked as such
                .drop(new Filter<TableMetadata<?>>() {
                    @Override
                    public boolean accepts(TableMetadata<?> item) {
                        return item.getEntityType().isAnnotationPresent(Ignored.class);
                    }
                })
                //ignore any item for which a table definition already exists
                .drop(new Filter<TableMetadata<?>>() {
                    @Override
                    public boolean accepts(TableMetadata<?> item) {
                        return isDefined(item);
                    }
                })
                .list();
        with(tables)
                .each(new Processor<TableMetadata<?>>() {
                    @Override
                    public void process(TableMetadata<?> input) {
                        defineTable(input);
                    }
                })
                .each(new Processor<TableMetadata<?>>() {
                    @Override
                    public void process(TableMetadata<?> input) {
                        definePrimaryKey(input);
                    }
                })
                .each(new Processor<TableMetadata<?>>() {
                    @Override
                    public void process(TableMetadata<?> input) {
                        defineSequences(input);
                    }
                })
                .each(new Processor<TableMetadata<?>>() {
                    @Override
                    public void process(TableMetadata<?> input) {
                        bindSequences(input);
                    }
                })
                .each(new Processor<TableMetadata<?>>() {
                    @Override
                    public void process(TableMetadata<?> input) {
                        defineUniqueConstraints(input);
                    }
                })
                .each(new Processor<TableMetadata<?>>() {
                    @Override
                    public void process(TableMetadata<?> input) {
                        defineForeignKeys(input);
                    }
                });
    }

}
