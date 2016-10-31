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

import com.mmnaseri.dragonfly.data.BatchOperation;
import com.mmnaseri.dragonfly.data.DataAccess;
import com.mmnaseri.dragonfly.sample.entities.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/9/25, 0:21)
 */
@Service
public class BatchOperationTest extends BaseTestCase {

    public static final int BENCHMARK_SIZE = 10000;

    @Autowired
    private DataAccess dataAccess;

    private Group getGroup() {
        final Group group = new Group();
        group.setName("This Group");
        return group;
    }

    @Override
    public void run() {
        //0.00025
        dataAccess.run(new BatchOperation() {
            @Override
            public void execute(DataAccess dataAccess) {
                final Group group = getGroup();
                for (int i = 0; i < BENCHMARK_SIZE; i++) {
                    dataAccess.save(group);
                }
            }
        });
        dataAccess.delete(getGroup());
        //0.0084862 (x33)
        for (int i = 0; i < BENCHMARK_SIZE; i ++) {
            dataAccess.save(getGroup());
        }
        dataAccess.deleteAll(Group.class);
    }

}
