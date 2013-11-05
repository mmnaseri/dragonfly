package com.agileapes.dragonfly.data;

/**
 * This interface indicates an operation that is taking place through a data access interface.
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/26, 2:21)
 */
public interface DataOperation {

    /**
     * @return the type of the operation taking place
     */
    OperationType getOperationType();

    /**
     * @return the data access instance through which the operation is taking place
     */
    DataAccess getDataAccess();

    /**
     * Should be called to indicate that the operation must be forfeited.
     */
    void interrupt();

    /**
     * Tells the encapsulated operation to proceed as intended
     * @return the results of the operation, or {@code null} if no results was returned
     */
    Object proceed();

}
