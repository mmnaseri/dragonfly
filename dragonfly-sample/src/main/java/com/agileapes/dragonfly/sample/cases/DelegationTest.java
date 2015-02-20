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

import com.agileapes.dragonfly.data.DataAccess;
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
public class DelegationTest extends BaseTestCase {

    @Autowired
    private DataAccess dataAccess;

    @Autowired
    private LogContainer logContainer;

    @Override
    public void run() {
        expect(dataAccess.save(new Memorable("First")).getId()).not().toBeNull();
        expect(dataAccess.save(new Memorable("Second")).getId()).not().toBeNull();
        expect(dataAccess.find(Memorable.class, 0L).getName()).toEqual("First");
        dataAccess.save(new Memorable(0L, "The First"));
        expect(dataAccess.find(Memorable.class, 0L).getName()).toEqual("The First");
        expect(dataAccess.find(Memorable.class, 1L).getName()).toEqual("Second");
        expect(dataAccess.countAll(Memorable.class)).toBe(2L);
        dataAccess.delete(new Memorable("Second"));
        expect(dataAccess.countAll(Memorable.class)).toEqual(1L);
        final List<LogEntry> logEntries = logContainer.getEntries();
        for (LogEntry logEntry : logEntries) {
            expect(logEntry).not().toBeNull();
        }
    }

}
