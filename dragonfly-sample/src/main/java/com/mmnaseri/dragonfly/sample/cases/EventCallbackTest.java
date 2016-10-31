/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Milad Naseri.
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

package com.mmnaseri.dragonfly.sample.cases;

import com.mmnaseri.dragonfly.data.DataAccess;
import com.mmnaseri.dragonfly.error.OptimisticLockingFailureError;
import com.mmnaseri.dragonfly.sample.entities.Station;
import com.mmnaseri.dragonfly.sample.test.Invocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/10/11, 13:06)
 */
@Service
public class EventCallbackTest extends BaseTestCase {

    @Autowired
    private DataAccess dataAccess;

    @Override
    public void run() {
        final Station station = new Station();
        station.setName("My Station");
        dataAccess.save(station);
        expect(station.getId()).not().toBeNull();
        expect(station.getCreationDate()).not().toBeNull();
        dataAccess.save(station);
        expect(station.getUpdateDate()).not().toBeNull();
        final Station anotherStation = new Station();
        anotherStation.setId(station.getId());
        final List<Station> stations = dataAccess.find(anotherStation);
        for (Station found : stations) {
            dataAccess.save(found);
        }
        final Station secondStation = new Station();
        secondStation.setId(station.getId());
        secondStation.setVersion(station.getVersion() - 1);
        expect(new Invocation() {
            @Override
            public void invoke() throws Exception {
                dataAccess.save(secondStation);
            }
        }).toThrow(OptimisticLockingFailureError.class);
        dataAccess.deleteAll(Station.class);
    }

}
