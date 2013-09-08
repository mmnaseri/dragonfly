package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/8/31, 17:36)
 */
public class MetadataAccessException extends DatabaseError {

    public MetadataAccessException(Throwable cause) {
        super("Error accessing metadata for table", cause);
    }

}
