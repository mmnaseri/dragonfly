package com.agileapes.dragonfly.statement.impl;

import com.agileapes.dragonfly.error.UnsupportedStatementTypeError;
import com.agileapes.dragonfly.statement.StatementBuilder;
import com.agileapes.dragonfly.statement.StatementBuilderContext;
import com.agileapes.dragonfly.statement.Statements;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 1:16)
 */
public class FreemarkerStatementBuilderContext implements StatementBuilderContext {

    private final Map<Statements.Definition, StatementBuilder> definitionStatements = new ConcurrentHashMap<Statements.Definition, StatementBuilder>();
    private final Map<Statements.Manipulation, StatementBuilder> manipulationStatements = new ConcurrentHashMap<Statements.Manipulation, StatementBuilder>();

    @Override
    public StatementBuilder getDefinitionStatementBuilder(Statements.Definition type) {
        if (definitionStatements.containsKey(type)) {
            return definitionStatements.get(type);
        }
        throw new UnsupportedStatementTypeError(type);
    }

    @Override
    public StatementBuilder getManipulationStatementBuilder(Statements.Manipulation type) {
        if (manipulationStatements.containsKey(type)) {
            return manipulationStatements.get(type);
        }
        throw new UnsupportedStatementTypeError(type);
    }

    public void register(Statements.Definition type, StatementBuilder statementBuilder) {
        definitionStatements.put(type, statementBuilder);
    }

    public void register(Statements.Manipulation type, StatementBuilder statementBuilder) {
        manipulationStatements.put(type, statementBuilder);
    }

}
