package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/29, 14:49)
 */
public class UnresolvedTableMetadataError extends DatabaseError {

    public UnresolvedTableMetadataError() {
        super("Table metadata has not been resolved yet");
    }
}
