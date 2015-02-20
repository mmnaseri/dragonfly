/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.agileapes.dragonfly.metadata;

/**
 * This enum will represent the different types of relations foreseeable between two
 * entity tables.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/12, 0:11)
 */
public enum RelationType {

    /**
     * A one-to-one relation is a relation in which rows of the two tables must correspond
     * directly. Since the opposite of this relation is itself, for the relation specification
     * to be complete, you will need to also know which side is the owner of the relation.
     */
    ONE_TO_ONE(1, 1),
    /**
     * In a one-to-many relation a single row of the local table will be pointed to by some
     * rows of the foreign table. In this case, it is clear that the owner of the relation
     * must be the <em>many</em> side.
     */
    ONE_TO_MANY(1, Integer.MAX_VALUE),
    /**
     * This is the opposite of the {@link #ONE_TO_MANY} relation, and as such, the local table
     * will clearly hold the pointing column, and as such, by the owner of the relation.
     */
    MANY_TO_ONE(Integer.MAX_VALUE, 1),
    /**
     * This is the many-to-many relation specifier, in which physical pointers from local rows
     * to foreign rows will have to be stored in a third table, and as such, none of the two
     * parties will be the owner of the relation.
     */
    MANY_TO_MANY(Integer.MAX_VALUE, Integer.MAX_VALUE);

    private final int localCardinality;
    private final int foreignCardinality;

    private RelationType(int localCardinality, int foreignCardinality) {
        this.localCardinality = localCardinality;
        this.foreignCardinality = foreignCardinality;
    }

    /**
     * @return specifies the number of rows possibly participating in the relation from
     * the local table
     */
    public int getLocalCardinality() {
        return localCardinality;
    }

    /**
     * @return specifies the possible number of rows for the foreign table that will take
     * part in the relation
     */
    public int getForeignCardinality() {
        return foreignCardinality;
    }

}
