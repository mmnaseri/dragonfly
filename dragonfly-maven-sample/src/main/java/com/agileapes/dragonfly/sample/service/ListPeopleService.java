package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.sample.entities.Person;
import com.agileapes.dragonfly.sample.entities.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/14, 0:47)
 */
@Service
public class ListPeopleService {

    @Autowired
    private DataAccess dataAccess;

    public void execute() {
        final List<Person> people = dataAccess.findAll(Person.class);
        for (Person person : people) {
            System.out.println("Person: " + person.getName());
            for (Thing thing : person.getThings()) {
                System.out.println("Thing: " + thing.getName());
            }
        }
    }

}
