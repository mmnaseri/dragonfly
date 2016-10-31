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
import com.mmnaseri.dragonfly.sample.entities.Author;
import com.mmnaseri.dragonfly.sample.entities.Book;
import com.mmnaseri.dragonfly.sample.entities.Person;
import com.mmnaseri.dragonfly.sample.entities.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (2013/11/3, 17:03)
 */
@Service
public class OrderingTest extends BaseTestCase {

    @Autowired
    private DataAccess dataAccess;

    @Override
    public void run() {
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
        dataAccess.deleteAll(Person.class);
        dataAccess.deleteAll(Book.class);
    }

}
