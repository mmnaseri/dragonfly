/*
 * Copyright (c) 2013 AgileApes, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 */

package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccessObject;
import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.sample.entities.Group;
import com.agileapes.dragonfly.sample.entities.LibraryCard;
import com.agileapes.dragonfly.sample.entities.Person;
import com.agileapes.dragonfly.sample.entities.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 23:06)
 */
@Service
public class CreatePersonService {

    @Autowired
    private EntityContext entityContext;

    private final class Tester implements Runnable {
        @Override
        public void run() {
            final Person person = entityContext.getInstance(Person.class);
            person.setLibraryCard(new LibraryCard());
            person.setName("Person - " + Math.abs((new Random().nextInt())));
            person.setBirthday(new Date());
            final Group group = new Group();
            group.setName("Normal People");
            person.setGroup(group);
            final ArrayList<Thing> things = new ArrayList<Thing>();
            person.setThings(things);
            for (int i = 0; i < 3; i ++) {
                final Thing thing = entityContext.getInstance(Thing.class);
                thing.setName("Thing - " + Math.abs((new Random().nextInt())));
                things.add(thing);
            }
            ((DataAccessObject) person).save();
        }
    }

    public void execute() {
        final Collection<Thread> threads = new HashSet<Thread>();
        for (int i = 0; i < 100; i ++) {
            threads.add(new Thread(new Tester()));
        }
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }

}
