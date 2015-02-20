/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.agileapes.dragonfly.sample.cases;

import com.agileapes.couteau.basics.api.Filter;
import com.agileapes.dragonfly.sample.assets.StationRepository;
import com.agileapes.dragonfly.sample.entities.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/14 AD, 13:13)
 */
@Service
public class CrudRepositoryTest extends BaseTestCase {

    public static final int SIZE = 10;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private StationRepository stationRepository;

    private Station getStation(int number) {
        final Station station = new Station();
        station.setName("Station " + number);
        return station;
    }

    @Override
    public void run() {
        for (int i = 0; i < SIZE; i ++) {
            stationRepository.save(getStation(i));
        }
        expect(stationRepository.findAll()).toHaveSize(SIZE);
        stationRepository.deleteEverything();
        expect(stationRepository.findAll()).toBeEmpty();
        for (int i = 0; i < SIZE; i ++) {
            stationRepository.save(getStation(i));
        }
        expect(stationRepository.findAll()).toHaveSize(SIZE);
        final Station station = stationRepository.findByNameAndVersion("Station 2", 0);
        expect(station).not().toBeNull();
        final List<Station> stations = stationRepository.findByName("Station 1");
        expect(stations).toHaveSize(1);
        stationRepository.deleteByNameAndVersion("Station 2", 0);
        expect(stationRepository.findByNameAndVersion("Station 2", 0)).toBeNull();
        stationRepository.deleteAll();
        Station first = stationRepository.save(getStation(0));
        first = stationRepository.save(first);
        first = stationRepository.save(first);
        Station second = stationRepository.save(getStation(1));
        second = stationRepository.save(second);
        second = stationRepository.save(second);
        Station third = stationRepository.save(getStation(1));
        third = stationRepository.save(third);
        expect(first.getVersion()).toBe(3);
        expect(second.getVersion()).toBe(3);
        expect(third.getVersion()).toBe(2);
        final List<Station> found = stationRepository.findNewerThan(1);
        expect(found).toHaveSize(2);
        expect(found).toContain(new Filter<Station>() {
            @Override
            public boolean accepts(Station item) {
                return item.getName().equals("Station 0");
            }
        });
        expect(found).toContain(new Filter<Station>() {
            @Override
            public boolean accepts(Station item) {
                return item.getName().equals("Station 1");
            }
        });
        expect(stationRepository.findNewerThan(1)).toEqual(stationRepository.findFresherThan(1));
        stationRepository.removeAll();
        expect(stationRepository.findNewerThan(0)).toBeEmpty();
    }

}
