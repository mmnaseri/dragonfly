/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.sample.assets;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/5, 10:52)
 */
@Repository
public class LogContainer {

    private final ThreadLocal<List<LogEntry>> entries = new ThreadLocal<List<LogEntry>>() {
        @Override
        protected List<LogEntry> initialValue() {
            return new ArrayList<LogEntry>();
        }
    };

    public void log(LogEntry entry) {
        entries.get().add(entry);
    }

    public List<LogEntry> getEntries() {
        return Collections.unmodifiableList(entries.get());
    }

}
