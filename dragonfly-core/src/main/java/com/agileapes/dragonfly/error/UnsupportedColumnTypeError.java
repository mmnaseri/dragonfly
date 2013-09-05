package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/5, 13:58)
 */
public class UnsupportedColumnTypeError extends DatabaseError {

    public UnsupportedColumnTypeError(String msg) {
        super(msg);
    }

}
