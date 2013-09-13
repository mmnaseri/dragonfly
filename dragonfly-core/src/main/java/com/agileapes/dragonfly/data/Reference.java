package com.agileapes.dragonfly.data;

/**
 * This class holds references to an underlying value that must be held until retrieved later
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/10, 1:04)
 */
public final class Reference<E> {

    private E value;

    public Reference() {
        this(null);
    }

    public Reference(E value) {
        this.value = value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public E getValue() {
        return value;
    }

}
