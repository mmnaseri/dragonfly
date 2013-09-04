package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/4, 15:32)
 */
public class DatabaseMetadataAccessError extends DatabaseError {

    public DatabaseMetadataAccessError(Throwable cause) {
        super("There was an error accessing database metadata", cause);
    }

}
