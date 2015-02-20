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

package com.agileapes.dragonfly.fluent.generation;

/**
 * Supported comparison operations
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/8 AD, 19:42)
 */
public enum ComparisonType {

    IS_EQUAL_TO("="),
    IS_GREATER_THAN(">"),
    IS_LESS_THAN("<"),
    IS_LIKE("LIKE"),
    IS_NULL("IS NULL"),
    IS_NOT_EQUAL_TO("!="),
    IS_LESS_THAN_OR_EQUAL_TO("<="),
    IS_GREATER_THAN_OR_EQUAL_TO(">="),
    IS_NOT_LIKE("NOT LIKE"),
    IS_NOT_NULL("IS NOT NULL"),
    IS_IN("IN"),
    IS_NOT_IN("NOT IN");
    private final String operator;

    ComparisonType(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

}
