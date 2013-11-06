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

package com.agileapes.dragonfly.annotations;

/**
 * This enum holds the different values possible for ordering of enumerable items
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/30, 11:07)
 */
public enum Ordering {

    ASCENDING("ASC"), DESCENDING("DESC");

    private final String text;

    private Ordering(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
