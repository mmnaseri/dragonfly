package com.agileapes.dragonfly.sample.assets;

import com.agileapes.dragonfly.sample.entities.Station;

import javax.persistence.PostUpdate;
import java.util.Date;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/11, 13:05)
 */
public class StationUpdateMonitor {

    @PostUpdate
    public void setUpdateDate(Station station) {
        station.setUpdateDate(new Date());
    }

}
