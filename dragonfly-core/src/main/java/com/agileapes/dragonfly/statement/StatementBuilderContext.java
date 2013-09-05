package com.agileapes.dragonfly.statement;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 0:57)
 */
public interface StatementBuilderContext {

    StatementBuilder getDefinitionStatementBuilder(Statements.Definition type);

    StatementBuilder getManipulationStatementBuilder(Statements.Manipulation type);

}
