package com.agileapes.dragonfly.statement;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/4, 18:09)
 */
public enum StatementType {

    QUERY("SELECT"), INSERT("INSERT"), UPDATE("UPDATE"), DELETE("DELETE"), TRUNCATE("TRUNCATE"), DEFINITION("<none>"), CALL("CALL");

    private final String starter;

    private StatementType(String starter) {
        this.starter = starter;
    }

    public static StatementType getStatementType(String statement) {
        if (statement.length() < 6) {
            return DEFINITION;
        }
        final String starter = statement.trim().substring(0, 6).toUpperCase();
        for (StatementType type : StatementType.values()) {
            if (type.starter.startsWith(starter)) {
                return type;
            }
        }
        if (starter.matches("CALL\\s.*")) {
            return CALL;
        }
        return DEFINITION;
    }

}
