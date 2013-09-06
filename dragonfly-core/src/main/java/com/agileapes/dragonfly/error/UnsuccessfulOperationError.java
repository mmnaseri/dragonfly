package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/7, 1:27)
 */
public class UnsuccessfulOperationError extends DatabaseError {

    public UnsuccessfulOperationError(String message) {
        super(message);
    }

    public UnsuccessfulOperationError(String message, Throwable cause) {
        super(message, cause);
    }

}
