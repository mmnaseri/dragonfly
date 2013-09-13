package com.agileapes.dragonfly.model;

import com.agileapes.dragonfly.statement.Statement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 2:32)
 */
public class StatementGenerationModel {

    private final Map<String, Statement> statements = new HashMap<String, Statement>();
    private final EscapeStringMethod escape = new EscapeStringMethod();

    public Map<String, Statement> getStatements() {
        return statements;
    }

    public void addStatement(String name, Statement statement) {
        this.statements.put(name, statement);
    }

    public EscapeStringMethod getEscape() {
        return escape;
    }
}
