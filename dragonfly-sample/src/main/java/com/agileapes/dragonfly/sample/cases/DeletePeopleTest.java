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
import com.agileapes.dragonfly.sample.entities.LibraryCard;
import com.agileapes.dragonfly.sample.entities.Person;
import com.agileapes.dragonfly.sample.entities.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 0:58)
 */
@Service
public class DeletePeopleTest extends BaseTestCase {

    @Autowired
    private DataAccess dataAccess;

    @Override
    public void run() {
        expect(dataAccess.findAll(Person.class)).toBeEmpty();
        final int size = 10;
        for (int i = 0; i < size; i ++) {
            savePerson();
        }
        expect(dataAccess.findAll(Person.class)).toHaveSize(size);
        dataAccess.deleteAll(Person.class);
        expect(dataAccess.findAll(Person.class)).toBeEmpty();
    }

    private void savePerson() {
        Person person = new Person();
        person.setBirthday(new Date());
        person = dataAccess.save(person);
        final LibraryCard libraryCard = new LibraryCard();
        libraryCard.setCardNumber(UUID.randomUUID().toString());
        libraryCard.setOwner(person);
        dataAccess.save(libraryCard);
    }

    private Thing getThing(Person person) {
        final Thing thing = new Thing();
        thing.setDescription("Description");
        thing.setName("Name");
        thing.setType(null);
        thing.setOwner(person);
        return thing;
    }

}
