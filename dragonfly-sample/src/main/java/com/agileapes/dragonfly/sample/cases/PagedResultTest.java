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

import com.mmnaseri.couteau.basics.api.Filter;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.sample.entities.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/21 AD, 13:37)
 */
@Service
public class PagedResultTest extends BaseTestCase {

    @Autowired
    private DataAccess dataAccess;

    private Station getStation(int id) {
        final Station station = new Station();
        station.setName("Station " + id);
        return station;
    }

    @Override
    public void run() {
        dataAccess.deleteAll(Station.class);
        for (int i = 0; i < 100; i ++) {
            dataAccess.insert(getStation(i));
        }
        final List<Station> stations = dataAccess.find(new Station(), 10, 2);
        for (int i = 10; i < 20; i ++) {
            final String stationName = "Station " + i;
            expect(with(stations).exists(new Filter<Station>() {
                @Override
                public boolean accepts(Station item) {
                    return item.getName().equals(stationName);
                }
            })).toBeTrue();
        }
    }

}
