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

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.error.OptimisticLockingFailureError;
import com.agileapes.dragonfly.sample.entities.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/11, 13:06)
 */
@Service
public class EventCallbackService {

    @Autowired
    private DataAccess dataAccess;

    public void execute() {
        final Station station = new Station();
        station.setName("My Station");
        dataAccess.save(station);
        System.out.println("Saved station with id: " + station.getId());
        System.out.println("Station inserted at: " + station.getCreationDate());
        dataAccess.save(station);
        System.out.println("Station updated at: " + station.getUpdateDate());
        final Station anotherStation = new Station();
        anotherStation.setId(station.getId());
        final List<Station> stations = dataAccess.find(anotherStation);
        for (Station found : stations) {
            dataAccess.save(found);
        }
        final Station secondStation = new Station();
        secondStation.setId(station.getId());
        secondStation.setVersion(station.getVersion());
        boolean error = false;
        try {
            dataAccess.save(secondStation);
        } catch (OptimisticLockingFailureError e) {
            error = true;
        }
        if (!error) {
            throw new Error("Expected optimistic locking to fail");
        }
    }

}
