package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.PartialDataAccess;
import com.agileapes.dragonfly.sample.assets.PeopleCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/21, 15:12)
 */
@Service
public class CountPeopleService {

    @Autowired
    private PartialDataAccess dataAccess;

    public void execute() {
        final List<PeopleCounter> counters = dataAccess.executeQuery(PeopleCounter.class);
        for (PeopleCounter counter : counters) {
            System.out.println("counter.getCount() = " + counter.getCount());
        }
    }

}
