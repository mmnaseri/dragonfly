package com.agileapes.dragonfly.events;

import com.agileapes.dragonfly.data.DataAccess;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/9, 2:37)
 */
public interface DataAccessPostProcessor {

    void postProcessAfterInitialization(DataAccess dataAccess);

}
