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
