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

import com.mmnaseri.couteau.context.contract.OrderedBean;
import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.sample.entities.Book;
import com.agileapes.dragonfly.sample.test.Always;
import com.agileapes.dragonfly.sample.test.Invocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/10/3, 11:08)
 */
@Service
@Always
public class CleanUpTest extends BaseTestCase implements OrderedBean {

    @Autowired
    private DataAccess dataAccess;

    @Override
    public void run() {
        expect(new Invocation() {
            @Override
            public void invoke() throws Exception {
                dataAccess.call(Book.class, "refresh");
            }
        }).not().toThrow();
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
