package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.impl.DelegatingDataAccess;
import com.agileapes.dragonfly.sample.assets.LogContainer;
import com.agileapes.dragonfly.sample.assets.LogEntry;
import com.agileapes.dragonfly.sample.assets.Memorable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/23, 12:55)
 */
@Service
public class DelegationService {

    @Autowired
    private DataAccess dataAccess;

    @Autowired
    private LogContainer logContainer;

    public void execute() {
        final DelegatingDataAccess dataAccess = new DelegatingDataAccess(this.dataAccess);
        System.out.println("first        : " + dataAccess.save(new Memorable("First")).getId());
        System.out.println("second       : " + dataAccess.save(new Memorable("Second")).getId());
        System.out.println("id (1)       : " + dataAccess.find(Memorable.class, 0L).getName());
        dataAccess.save(new Memorable(0L, "The First"));
        System.out.println("id (1)       : " + dataAccess.find(Memorable.class, 0L).getName());
        System.out.println("id (2)       : " + dataAccess.find(Memorable.class, 1L).getName());
        System.out.println("count all    : " + dataAccess.countAll(Memorable.class));
        System.out.println("delete (1)");
        dataAccess.delete(new Memorable("Second"));
        System.out.println("count all    : " + dataAccess.countAll(Memorable.class));
        final List<LogEntry> logEntries = logContainer.getEntries();
        for (LogEntry logEntry : logEntries) {
            System.out.println(logEntry);
        }

    }

}
