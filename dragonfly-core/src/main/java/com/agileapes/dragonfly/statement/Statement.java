package com.agileapes.dragonfly.statement;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 17:58)
 */
public interface Statement {

    boolean isDynamic();

    boolean hasParameters();

    String getSql();

    StatementType getType();

}
