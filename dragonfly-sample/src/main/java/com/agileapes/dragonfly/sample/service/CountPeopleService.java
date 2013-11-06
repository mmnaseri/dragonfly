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
