package com.agileapes.dragonfly.data.impl;

import com.agileapes.couteau.basics.api.Processor;
import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.data.DataStructureHandler;
import com.agileapes.dragonfly.error.UnknownTableSchemaError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.Metadata;
import com.agileapes.dragonfly.metadata.SequenceMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
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

    private <E> void executeStatement(Class<E> entityType, Statements.Definition statementType, Metadata metadata) {
        executeStatement(session.getMetadataRegistry().getTableMetadata(entityType), statementType, metadata);
    }

    private <E> void executeStatement(TableMetadata<E> tableMetadata, Statements.Definition statementType, Metadata constraintMetadata) {
        try {
            final Statement statement = session.getDatabaseDialect().getStatementBuilderContext().getDefinitionStatementBuilder(statementType).getStatement(tableMetadata, constraintMetadata);
            final PreparedStatement preparedStatement = statement.prepare(session.getConnection());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <E> void defineTable(Class<E> entityType) {
        log.info("Defining table for type " + entityType.getCanonicalName());
        executeStatement(entityType, CREATE_TABLE, null);
    }

    @Override
    public <E> void definePrimaryKey(Class<E> entityType) {
        log.info("Defining primary key for " + entityType.getCanonicalName());
        final TableMetadata<E> tableMetadata = session.getMetadataRegistry().getTableMetadata(entityType);
        if (tableMetadata.hasPrimaryKey()) {
            executeStatement(entityType, CREATE_PRIMARY_KEY, null);
        }
    }

    @Override
    public <E> void defineSequences(final Class<E> entityType) {
        final TableMetadata<E> tableMetadata = session.getMetadataRegistry().getTableMetadata(entityType);
        with(tableMetadata.getSequences()).each(new Processor<SequenceMetadata>() {
            @Override
            public void process(SequenceMetadata sequenceMetadata) {
                log.info("Defining sequence (" + sequenceMetadata.getName() + ") on " + entityType.getCanonicalName());
                executeStatement(tableMetadata, CREATE_SEQUENCE, sequenceMetadata);
            }
        });
    }

    @Override
    public <E> void defineForeignKeys(final Class<E> entityType) {
        final TableMetadata<E> tableMetadata = session.getMetadataRegistry().getTableMetadata(entityType);
        with(tableMetadata.getConstraints(ForeignKeyConstraintMetadata.class)).each(new Processor<ForeignKeyConstraintMetadata>() {
            @Override
            public void process(ForeignKeyConstraintMetadata foreignKeyConstraintMetadata) {
                log.info("Defining foreign key constraint (" + foreignKeyConstraintMetadata.getName() + ") for " + entityType.getCanonicalName());
                executeStatement(tableMetadata, CREATE_FOREIGN_KEY, foreignKeyConstraintMetadata);
            }
        });
    }

    @Override
    public <E> void defineUniqueConstraints(final Class<E> entityType) {
        final TableMetadata<E> tableMetadata = session.getMetadataRegistry().getTableMetadata(entityType);
        with(tableMetadata.getConstraints(UniqueConstraintMetadata.class)).each(new Processor<UniqueConstraintMetadata>() {
            @Override
            public void process(UniqueConstraintMetadata uniqueConstraintMetadata) {
                log.info("Defining unique constraint (" + uniqueConstraintMetadata.getName() + ") on " + entityType.getCanonicalName());
                executeStatement(tableMetadata, CREATE_UNIQUE_CONSTRAINT, uniqueConstraintMetadata);
            }
        });
    }

    @Override
    public <E> void removeTable(Class<E> entityType) {
        log.warn("Removing table " + entityType.getCanonicalName());
        executeStatement(entityType, DROP_TABLE, null);
    }

    @Override
    public <E> void removePrimaryKeys(Class<E> entityType) {
        log.info("Removing primary key definition " + entityType.getCanonicalName());
        executeStatement(entityType, DROP_PRIMARY_KEY, null);
    }

    @Override
    public <E> void removeSequences(final Class<E> entityType) {
        final TableMetadata<E> tableMetadata = session.getMetadataRegistry().getTableMetadata(entityType);
        with(tableMetadata.getSequences()).each(new Processor<SequenceMetadata>() {
            @Override
            public void process(SequenceMetadata sequenceMetadata) {
                log.info("Removing sequence metadata " + sequenceMetadata.getName() + " from " + entityType.getCanonicalName());
                executeStatement(tableMetadata, DROP_SEQUENCE, sequenceMetadata);
            }
        });
    }

    @Override
    public <E> void removeForeignKeys(final Class<E> entityType) {
        final TableMetadata<E> tableMetadata = session.getMetadataRegistry().getTableMetadata(entityType);
        with(tableMetadata.getConstraints(ForeignKeyConstraintMetadata.class)).each(new Processor<ForeignKeyConstraintMetadata>() {
            @Override
            public void process(ForeignKeyConstraintMetadata foreignKeyConstraintMetadata) {
                log.info("Removing foreign key metadata " + foreignKeyConstraintMetadata.getName() + " from " + entityType.getCanonicalName());
                executeStatement(tableMetadata, DROP_FOREIGN_KEY, foreignKeyConstraintMetadata);
            }
        });
    }

    @Override
    public <E> void removeUniqueConstraints(final Class<E> entityType) {
        final TableMetadata<E> tableMetadata = session.getMetadataRegistry().getTableMetadata(entityType);
        with(tableMetadata.getConstraints(UniqueConstraintMetadata.class)).each(new Processor<UniqueConstraintMetadata>() {
            @Override
            public void process(UniqueConstraintMetadata uniqueConstraintMetadata) {
                log.info("Removing unique constraint metadata " + uniqueConstraintMetadata.getName() + " from " + entityType.getCanonicalName());
                executeStatement(tableMetadata, DROP_UNIQUE_CONSTRAINT, uniqueConstraintMetadata);
            }
        });
    }

    @Override
    public <E> void bindSequences(final Class<E> entityType) {
        final TableMetadata<E> tableMetadata = session.getMetadataRegistry().getTableMetadata(entityType);
        with(tableMetadata.getColumns()).keep(new SequenceColumnFilter()).each(new Processor<ColumnMetadata>() {
            @Override
            public void process(ColumnMetadata columnMetadata) {
                log.info("Binding sequences to " + columnMetadata.getName() + " on " + entityType.getCanonicalName());
                executeStatement(tableMetadata, BIND_SEQUENCE, columnMetadata);
            }
        });
    }

    @Override
    public <E> void unbindSequences(final Class<E> entityType) {
        final TableMetadata<E> tableMetadata = session.getMetadataRegistry().getTableMetadata(entityType);
        with(tableMetadata.getColumns()).keep(new SequenceColumnFilter()).each(new Processor<ColumnMetadata>() {
            @Override
            public void process(ColumnMetadata columnMetadata) {
                log.info("Unbinding sequences from " + columnMetadata.getName() + " on " + entityType.getCanonicalName());
                executeStatement(tableMetadata, UNBIND_SEQUENCE, columnMetadata);
            }
        });
    }

    @Override
    public <E> boolean isDefined(Class<E> entityType) {
        try {
            final Connection connection = session.getConnection();
            final TableMetadata<E> tableMetadata = session.getMetadataRegistry().getTableMetadata(entityType);
            String schema = tableMetadata.getSchema();
            if (schema == null || schema.isEmpty()) {
                schema = connection.getCatalog();
            }
            if (schema == null || schema.isEmpty()) {
                throw new UnknownTableSchemaError(entityType);
            }
            return session.getDatabaseDialect().hasTable(connection.getMetaData(), tableMetadata);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void initialize() {
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
    }

}
