package com.agileapes.dragonfly.error;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/25, 0:05)
 */
public class BatchOperationInterruptedError extends DatabaseError {

    public BatchOperationInterruptedError(String message) {
        super(message);
    }

    public BatchOperationInterruptedError(String message, Throwable cause) {
        super(message, cause);
    }
}