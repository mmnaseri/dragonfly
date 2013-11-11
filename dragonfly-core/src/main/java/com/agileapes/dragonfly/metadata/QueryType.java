package com.agileapes.dragonfly.metadata;

/**
 * Declares the type of the query. This usually affects the chain of post-processing the query
 * will have to undergo before it can be submitted to the database
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/11/11, 19:01)
 */
public enum QueryType {

    /**
     * Indicates that the query has been written in the native language supported by
     * the database, or that it can be readily expanded to that dialect
     */
    NATIVE

}
