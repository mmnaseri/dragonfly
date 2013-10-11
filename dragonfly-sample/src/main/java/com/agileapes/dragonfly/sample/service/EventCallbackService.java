package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.sample.entities.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    }

}
