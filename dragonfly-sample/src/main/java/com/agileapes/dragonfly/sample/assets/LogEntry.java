package com.agileapes.dragonfly.sample.assets;

import com.agileapes.dragonfly.data.DataOperation;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/5, 10:49)
 */
public interface LogEntry {

    long getTime();

    DataOperation getOperation();

}
