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
