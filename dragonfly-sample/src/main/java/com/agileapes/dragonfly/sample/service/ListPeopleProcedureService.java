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

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.impl.Reference;
import com.agileapes.dragonfly.sample.entities.Person;
import com.agileapes.dragonfly.sample.entities.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/23, 0:01)
 */
@Service
public class ListPeopleProcedureService {

    @Autowired
    private DataAccess dataAccess;

    public void execute() {
        final Reference<Long> count = new Reference<Long>();
        final List<?> call = dataAccess.call(Person.class, "findPeopleByName", "Person", count);
        System.out.println("count.getValue() = " + count.getValue());
        for (Object object : call) {
            System.out.println(object);
            if (object instanceof Person) {
                Person person = (Person) object;
                for (Thing thing : person.getThings()) {
                    System.out.println(thing);
                }
                System.out.println(person.getLibraryCard());
            }
        }
    }

}
