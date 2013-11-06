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
import com.agileapes.dragonfly.sample.entities.Author;
import com.agileapes.dragonfly.sample.entities.Book;
import com.agileapes.dragonfly.sample.entities.Person;
import com.agileapes.dragonfly.sample.entities.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/11/3, 17:03)
 */
@Service
public class OrderingService {

    @Autowired
    private DataAccess dataAccess;

    public void execute() {
        dataAccess.find(new Person(), "key DESC , name ASC");
        final Book book = new Book();
        book.setAuthors(Arrays.asList(new Author(), new Author()));
        dataAccess.save(book);
        dataAccess.findAll(Book.class);
        final Person person = new Person();
        person.setThings(Arrays.asList(new Thing(), new Thing()));
        final Person saved = dataAccess.save(person);
        final List<Person> list = dataAccess.find(saved);
        for (Person found : list) {
            found.getThings();
        }
    }
}
