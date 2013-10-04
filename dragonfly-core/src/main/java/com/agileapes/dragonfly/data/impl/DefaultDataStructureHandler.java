package com.agileapes.dragonfly.data.impl;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.data.DataStructureHandler;
import com.agileapes.dragonfly.error.UnsuccessfulOperationError;
import com.agileapes.dragonfly.metadata.*;
import com.agileapes.dragonfly.metadata.impl.DefaultMetadataContext;
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
import java.util.HashSet;
import java.util.Set;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;
import static com.agileapes.dragonfly.statement.Statements.Definition.*;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/8, 14:31)
 */
public class DefaultDataStructureHandler implements DataStructureHandler {

    private static final Log log = LogFactory.getLog(DataStructureHandler.class);
    private final DataAccessSession session;

    public DefaultDataStructureHandler(DataAccessSession session) {
        this.session = session;
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
        defineTable(session.getMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void definePrimaryKey(Class<E> entityType) {
        definePrimaryKey(session.getMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void defineSequences(final Class<E> entityType) {
        defineSequences(session.getMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void defineForeignKeys(final Class<E> entityType) {
        defineForeignKeys(session.getMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void defineUniqueConstraints(final Class<E> entityType) {
        defineUniqueConstraints(session.getMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void removeTable(Class<E> entityType) {
        removeTable(session.getMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void removePrimaryKeys(Class<E> entityType) {
        removePrimaryKeys(session.getMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void removeSequences(final Class<E> entityType) {
        removeSequences(session.getMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void removeForeignKeys(final Class<E> entityType) {
        removeForeignKeys(session.getMetadataRegistry().getTableMetadata(entityType));
    }

    @Override

    public <E> void removeUniqueConstraints(final Class<E> entityType) {
        removeUniqueConstraints(session.getMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void bindSequences(final Class<E> entityType) {
        bindSequences(session.getMetadataRegistry().getTableMetadata(entityType));
    }

    @Override
    public <E> void unbindSequences(final Class<E> entityType) {
        unbindSequences(session.getMetadataRegistry().getTableMetadata(entityType));
    }

    public <E> void defineTable(TableMetadata<E> tableMetadata) {
        log.info("Defining table for type " + tableMetadata.getName());
        executeStatement(tableMetadata, CREATE_TABLE, null);
    }

    public <E> void definePrimaryKey(TableMetadata<E> tableMetadata) {
        log.info("Defining primary key for " + tableMetadata.getName());
        if (tableMetadata.hasPrimaryKey()) {
            executeStatement(tableMetadata, CREATE_PRIMARY_KEY, null);
        }
    }

    public <E> void defineSequences(final TableMetadata<E> tableMetadata) {
        with(tableMetadata.getSequences()).each(new Processor<SequenceMetadata>() {
            @Override
            public void process(SequenceMetadata sequenceMetadata) {
                log.info("Defining sequence (" + sequenceMetadata.getName() + ") on " + tableMetadata.getName());
                executeStatement(tableMetadata, CREATE_SEQUENCE, sequenceMetadata);
            }
        });
    }

    public <E> void defineForeignKeys(final TableMetadata<E> tableMetadata) {
        with(tableMetadata.getConstraints(ForeignKeyConstraintMetadata.class)).each(new Processor<ForeignKeyConstraintMetadata>() {
            @Override
            public void process(ForeignKeyConstraintMetadata foreignKeyConstraintMetadata) {
                log.info("Defining foreign key constraint (" + foreignKeyConstraintMetadata.getName() + ") for " + tableMetadata.getName());
                executeStatement(tableMetadata, CREATE_FOREIGN_KEY, foreignKeyConstraintMetadata);
            }
        });
    }

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

    public <E> void removeTable(TableMetadata<E> tableMetadata) {
        log.warn("Removing table " + tableMetadata.getName());
        executeStatement(tableMetadata, DROP_TABLE, null);
    }

    public <E> void removePrimaryKeys(TableMetadata<E> tableMetadata) {
        log.info("Removing primary key definition " + tableMetadata.getName());
        executeStatement(tableMetadata, DROP_PRIMARY_KEY, null);
    }

    public <E> void removeSequences(final TableMetadata<E> tableMetadata) {
        with(tableMetadata.getSequences()).each(new Processor<SequenceMetadata>() {
            @Override
            public void process(SequenceMetadata sequenceMetadata) {
                log.info("Removing sequence metadata " + sequenceMetadata.getName() + " from " + tableMetadata.getName());
                executeStatement(tableMetadata, DROP_SEQUENCE, sequenceMetadata);
            }
        });
    }

    public <E> void removeForeignKeys(final TableMetadata<E> tableMetadata) {
        with(tableMetadata.getConstraints(ForeignKeyConstraintMetadata.class)).each(new Processor<ForeignKeyConstraintMetadata>() {
            @Override
            public void process(ForeignKeyConstraintMetadata foreignKeyConstraintMetadata) {
                log.info("Removing foreign key metadata " + foreignKeyConstraintMetadata.getName() + " from " + tableMetadata.getName());
                executeStatement(tableMetadata, DROP_FOREIGN_KEY, foreignKeyConstraintMetadata);
            }
        });
    }

    public <E> void removeUniqueConstraints(final TableMetadata<E> tableMetadata) {
        with(tableMetadata.getConstraints(UniqueConstraintMetadata.class)).each(new Processor<UniqueConstraintMetadata>() {
            @Override
            public void process(UniqueConstraintMetadata uniqueConstraintMetadata) {
                log.info("Removing unique constraint metadata " + uniqueConstraintMetadata.getName() + " from " + tableMetadata.getName());
                executeStatement(tableMetadata, DROP_UNIQUE_CONSTRAINT, uniqueConstraintMetadata);
            }
        });
    }

    public <E> void bindSequences(final TableMetadata<E> tableMetadata) {
        with(tableMetadata.getColumns()).keep(new SequenceColumnFilter()).each(new Processor<ColumnMetadata>() {
            @Override
            public void process(ColumnMetadata columnMetadata) {
                log.info("Binding sequences to " + columnMetadata.getName() + " on " + tableMetadata.getName());
                executeStatement(tableMetadata, BIND_SEQUENCE, columnMetadata);
            }
        });
    }

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
        return isDefined(session.getMetadataRegistry().getTableMetadata(entityType));
    }

    private <E> boolean isDefined(TableMetadata<E> tableMetadata) {
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
        final Set<Class<?>> undefinedEntities = new HashSet<Class<?>>();
        final Collection<Class<?>> registeredEntities = session.getRegisteredEntities();
        for (Class<?> registeredEntity : registeredEntities) {
            if (!isDefined(registeredEntity)) {
                log.info("Found undefined entity " + registeredEntity.getCanonicalName());
                undefinedEntities.add(registeredEntity);
            }
        }
        for (Class<?> entity : undefinedEntities) {
            defineTable(entity);
        }
        for (Class<?> entity : undefinedEntities) {
            definePrimaryKey(entity);
        }
        for (Class<?> entity : undefinedEntities) {
            defineSequences(entity);
        }
        for (Class<?> entity : undefinedEntities) {
            bindSequences(entity);
        }
        for (Class<?> entity : undefinedEntities) {
            defineUniqueConstraints(entity);
        }
        for (Class<?> entity : undefinedEntities) {
            defineForeignKeys(entity);
        }
        if (session.getMetadataRegistry() instanceof DefaultMetadataContext) {
            DefaultMetadataContext context = (DefaultMetadataContext) session.getMetadataRegistry();
            final Set<TableMetadata<?>> tables = context.getVirtualTables();
            final Set<TableMetadata<?>> undefinedTables = new HashSet<TableMetadata<?>>();
            for (TableMetadata<?> table : tables) {
                if (!isDefined(table)) {
                    undefinedTables.add(table);
                }
            }
            for (TableMetadata<?> table : undefinedTables) {
                defineTable(table);
            }
            for (TableMetadata<?> table : undefinedTables) {
                definePrimaryKey(table);
            }
            for (TableMetadata<?> table : undefinedTables) {
                defineSequences(table);
            }
            for (TableMetadata<?> table : undefinedTables) {
                bindSequences(table);
            }
            for (TableMetadata<?> table : undefinedTables) {
                defineUniqueConstraints(table);
            }
            for (TableMetadata<?> table : undefinedTables) {
                defineForeignKeys(table);
            }
        }
    }

}
