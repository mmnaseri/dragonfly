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

import com.agileapes.couteau.basics.api.impl.NegatingFilter;
import com.agileapes.dragonfly.data.EntityPreparationCallback;
import com.agileapes.dragonfly.entity.*;
import com.agileapes.dragonfly.entity.impl.DefaultEntityMapCreator;
import com.agileapes.dragonfly.entity.impl.DefaultMapEntityCreator;
import com.agileapes.dragonfly.entity.impl.DefaultRowHandler;
import com.agileapes.dragonfly.error.UnsuccessfulOperationError;
import com.agileapes.dragonfly.metadata.ColumnMetadata;
import com.agileapes.dragonfly.metadata.RelationMetadata;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.statement.Statement;
import com.agileapes.dragonfly.statement.StatementBuilderContext;
import com.agileapes.dragonfly.statement.Statements;
import com.agileapes.dragonfly.tools.ColumnNameFilter;
import com.agileapes.dragonfly.tools.MapTools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.agileapes.couteau.basics.collections.CollectionWrapper.with;

/**
 * This class is designed as a helper for performing operations on the middle entities
 * in a many-to-many relation.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/3, 13:35)
 */
public class ManyToManyActionHelper {

    private final StatementPreparator statementPreparator;
    private final Connection connection;
    private final TableMetadata<?> tableMetadata;
    private final TableMetadata<?> currentTable;
    private final EntityContext entityContext;
    private final Statement insertStatement;
    private final Statement deleteStatement;
    private final Statement selectStatement;
    private final EntityMapCreator mapCreator;
    private final MapEntityCreator entityCreator;
    private final RowHandler rowHandler;
    private PreparedStatement preparedDeleteStatement;
    private PreparedStatement preparedInsertStatement;
    private PreparedStatement preparedSelectStatement;

    public ManyToManyActionHelper(StatementPreparator statementPreparator, Connection connection, StatementBuilderContext statementBuilderContext, TableMetadata<?> tableMetadata, TableMetadata<?> currentTable, RelationMetadata<?, ?> relationMetadata, EntityContext entityContext) {
        this.statementPreparator = statementPreparator;
        this.connection = connection;
        this.tableMetadata = tableMetadata;
        this.currentTable = currentTable;
        this.entityContext = entityContext;
        this.insertStatement = statementBuilderContext.getManipulationStatementBuilder(Statements.Manipulation.INSERT).getStatement(tableMetadata);
        this.deleteStatement = statementBuilderContext.getManipulationStatementBuilder(Statements.Manipulation.DELETE_LIKE).getStatement(tableMetadata);
        this.selectStatement = relationMetadata == null ? statementBuilderContext.getManipulationStatementBuilder(Statements.Manipulation.FIND_LIKE).getStatement(tableMetadata) : statementBuilderContext.getManipulationStatementBuilder(Statements.Manipulation.LOAD_MANY_TO_MANY).getStatement(tableMetadata, relationMetadata);
        this.mapCreator = new DefaultEntityMapCreator();
        this.rowHandler = new DefaultRowHandler();
        this.entityCreator = new DefaultMapEntityCreator();
    }

    public PreparedStatement getPreparedDeleteStatement(ManyToManyMiddleEntity entity) {
        //noinspection unchecked
        final Map<String, Object> map = mapCreator.toMap((TableMetadata<Object>) tableMetadata, entity);
        final Map<String, Object> value = new HashMap<String, Object>();
        final ColumnMetadata column = with(tableMetadata.getColumns()).find(new ColumnNameFilter(currentTable.getName()));
        value.put("value." + column.getPropertyName(), map.get(column.getPropertyName()));
        if (preparedDeleteStatement == null) {
            final PreparedStatement preparedStatement = deleteStatement.prepare(connection, null, value);
            this.preparedDeleteStatement = preparedStatement;
            return preparedStatement;
        } else {
            return statementPreparator.prepare(preparedDeleteStatement, tableMetadata, value, deleteStatement.getSql());
        }
    }

    public PreparedStatement getPreparedSelectStatement(ManyToManyMiddleEntity entity) {
        //noinspection unchecked
        final Map<String, Object> map = mapCreator.toMap((TableMetadata<Object>) tableMetadata, entity);
        final Map<String, Object> value = new HashMap<String, Object>();
        final ColumnMetadata column = with(tableMetadata.getColumns()).find(new ColumnNameFilter(currentTable.getName()));
        value.put("value." + column.getForeignReference().getName(), map.get(column.getPropertyName()));
        if (preparedSelectStatement == null) {
            final PreparedStatement preparedStatement = selectStatement.prepare(connection, null, value);
            this.preparedSelectStatement = preparedStatement;
            return preparedStatement;
        } else {
            return statementPreparator.prepare(preparedSelectStatement, tableMetadata, value, selectStatement.getSql());
        }
    }

    public PreparedStatement getPreparedInsertStatement(ManyToManyMiddleEntity entity) {
        //noinspection unchecked
        final Map<String, Object> value = MapTools.prefixKeys(mapCreator.toMap((TableMetadata<Object>) tableMetadata, entity), "value.");
        if (preparedInsertStatement == null) {
            final PreparedStatement preparedStatement = insertStatement.prepare(connection, null, value);
            this.preparedInsertStatement = preparedStatement;
            return preparedStatement;
        } else {
            return statementPreparator.prepare(preparedInsertStatement, tableMetadata, value, insertStatement.getSql());
        }
    }

    public void insert(ManyToManyMiddleEntity entity) {
        try {
            getPreparedInsertStatement(entity).executeUpdate();
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to insert relation", e);
        }
    }

    public void delete(ManyToManyMiddleEntity entity) {
        try {
            getPreparedDeleteStatement(entity).executeUpdate();
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to delete relations", e);
        }
    }

    public List<Object> find(ManyToManyMiddleEntity entity, EntityPreparationCallback callback) {
        try {
            final List<Object> result = new ArrayList<Object>();
            final ResultSet resultSet = getPreparedSelectStatement(entity).executeQuery();
            final TableMetadata<?> otherTable = with(tableMetadata.getColumns()).find(new NegatingFilter<ColumnMetadata>(new ColumnNameFilter(currentTable.getName()))).getForeignReference().getTable();
            while (resultSet.next()) {
                final Map<String, Object> map = rowHandler.handleRow(resultSet);
                final Object instance = entityCreator.fromMap(entityContext.getInstance(otherTable), otherTable.getColumns(), map);
                if (callback != null) {
                    callback.prepare(instance, map);
                }
                result.add(instance);
            }
            return result;
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to load relations", e);
        }
    }

    public void close() {
        try {
            if (preparedInsertStatement != null) {
                preparedInsertStatement.close();
            }
            if (preparedDeleteStatement != null) {
                preparedDeleteStatement.close();
            }
            if (preparedSelectStatement != null) {
                preparedSelectStatement.close();
            }
        } catch (SQLException e) {
            throw new UnsuccessfulOperationError("Failed to close statements", e);
        }
    }

}
