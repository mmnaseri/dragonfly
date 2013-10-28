package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.entity.EntityContext;
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

    @Autowired
    private EntityContext entityContext;

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
