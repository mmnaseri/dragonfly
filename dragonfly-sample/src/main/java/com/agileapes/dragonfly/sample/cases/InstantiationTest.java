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

import com.agileapes.dragonfly.data.DataAccessSession;
import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.entity.impl.EntityProxy;
import com.agileapes.dragonfly.entity.impl.GenericEntityHandler;
import com.agileapes.dragonfly.metadata.TableMetadata;
import com.agileapes.dragonfly.metadata.TableMetadataRegistry;
import com.agileapes.dragonfly.sample.entities.Person;
import com.agileapes.dragonfly.security.impl.DefaultDataSecurityManager;
import com.agileapes.dragonfly.security.impl.FailFirstAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/15, 16:56)
 */
@Service
public class InstantiationTest extends BaseTestCase {

    @Autowired
    private EntityContext entityContext;

    @Autowired
    private TableMetadataRegistry tableMetadataRegistry;

    @Autowired
    private DataAccessSession session;

    @Override
    public void run() {
        long normalTime = System.nanoTime();
        final int benchmarkSize = 10000;
        final DefaultDataSecurityManager securityManager = new DefaultDataSecurityManager(new FailFirstAccessDeniedHandler());
        for (int i = 0; i < benchmarkSize; i ++) {
            final TableMetadata<Person> tableMetadata = tableMetadataRegistry.getTableMetadata(Person.class);
            new EntityProxy<Person>(securityManager, tableMetadata, new GenericEntityHandler<Person>(Person.class, entityContext, tableMetadata), null, session, entityContext);
            new Person();
        }
        normalTime = System.nanoTime() - normalTime;
        long contextTime = System.nanoTime();
        for (int i = 0; i < benchmarkSize; i ++) {
            entityContext.getInstance(Person.class);
        }
        contextTime = System.nanoTime() - contextTime;
        System.out.println("Benchmark size: " + benchmarkSize);
        System.out.println("Normal time: " + normalTime);
        System.out.println("Context time: " + contextTime);
        System.out.println("[c/n] : " + (((double) contextTime) / normalTime));
    }

}
