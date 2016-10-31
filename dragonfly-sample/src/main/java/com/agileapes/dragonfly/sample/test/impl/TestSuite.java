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

package com.agileapes.dragonfly.sample.test.impl;

import com.mmnaseri.couteau.basics.api.Filter;
import com.mmnaseri.couteau.basics.api.Processor;
import com.mmnaseri.couteau.context.impl.OrderedBeanComparator;
import com.agileapes.dragonfly.annotations.Ignored;
import com.agileapes.dragonfly.sample.test.Always;
import com.agileapes.dragonfly.sample.test.Focus;
import com.agileapes.dragonfly.sample.test.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StopWatch;

import java.util.Collection;

import static com.mmnaseri.couteau.basics.collections.CollectionWrapper.with;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (14/8/30 AD, 15:32)
 */
@Repository
public class TestSuite implements Runnable {

    private static final Log log = LogFactory.getLog(TestSuite.class);
    private final StopWatch stopWatch;

    @Autowired
    private Collection<TestCase> cases;

    public TestSuite() {
        stopWatch = new StopWatch(TestSuite.class.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        if (with(cases).exists(new Filter<TestCase>() {
            @Override
            public boolean accepts(TestCase item) {
                return item.getClass().isAnnotationPresent(Focus.class);
            }
        })) {
            cases = with(cases).keep(new Filter<TestCase>() {
                @Override
                public boolean accepts(TestCase item) {
                    return item.getClass().isAnnotationPresent(Always.class) || item.getClass().isAnnotationPresent(Focus.class);
                }
            }).list();
        }
        with(cases).drop(new Filter<TestCase>() {
            @Override
            public boolean accepts(TestCase item) {
                return item.getClass().isAnnotationPresent(Ignored.class);
            }
        }).sort(new OrderedBeanComparator()).each(new Processor<TestCase>() {
            @Override
            public void process(TestCase testCase) {
                final String caseName = testCase.getClass().getSimpleName();
                log.info("Running test case " + caseName);
                stopWatch.start(caseName);
                testCase.execute();
                stopWatch.stop();
            }
        });
        log.info(stopWatch.prettyPrint());
    }

}
