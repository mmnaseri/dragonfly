package com.agileapes.dragonfly.metadata;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 0:11)
 */
public enum RelationType {

    ONE_TO_ONE(1, 1),
    ONE_TO_MANY(1, Integer.MAX_VALUE),
    MANY_TO_ONE(Integer.MAX_VALUE, 1),
    MANY_TO_MANY(Integer.MAX_VALUE, Integer.MAX_VALUE);

    private final int localCardinality;
    private final int foreignCardinality;

    private RelationType(int localCardinality, int foreignCardinality) {
        this.localCardinality = localCardinality;
        this.foreignCardinality = foreignCardinality;
    }

    public int getLocalCardinality() {
        return localCardinality;
    }

    public int getForeignCardinality() {
        return foreignCardinality;
    }

}
