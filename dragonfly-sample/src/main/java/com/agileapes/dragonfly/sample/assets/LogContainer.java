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
