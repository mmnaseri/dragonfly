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
