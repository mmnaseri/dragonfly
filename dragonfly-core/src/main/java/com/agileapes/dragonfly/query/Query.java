package com.agileapes.dragonfly.query;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/3, 17:58)
 */
public interface Query {

    boolean isDynamic();

    String getSql();

}
