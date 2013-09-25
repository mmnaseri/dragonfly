package com.agileapes.dragonfly.data;

/**
 * This interface allows for post processing of the data access, after it has been
 * initialized. To post process the data access before its initialization, you should
 * set the {@code autoInitialize} property of the data access to {@code false} and manually
 * initialize its session via {@link com.agileapes.dragonfly.data.impl.DataAccessSession#initialize()}
 *
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 2:37)
 */
public interface DataAccessPostProcessor {

    /**
     * Will be called to post process the data access instance
     * @param dataAccess    the data access to be post processed
     */
    void postProcessDataAccess(DataAccess dataAccess);

}
