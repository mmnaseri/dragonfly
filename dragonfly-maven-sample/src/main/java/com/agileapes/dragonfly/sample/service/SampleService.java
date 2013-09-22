package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.sample.entities.LibraryCard;
import com.agileapes.dragonfly.sample.entities.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/21, 15:12)
 */
@Service
public class SampleService {

    @Autowired
    private DataAccess dataAccess;

    public void execute() {
        dataAccess.deleteAll(LibraryCard.class);
        dataAccess.deleteAll(Person.class);
        Person person = new Person();
        person.setName("The Reader");
        person.setLibraryCard(new LibraryCard());
        person = dataAccess.save(person);
        System.out.println(person.getLibraryCard());
//        Person person = new Person();
//        person.setThings(Arrays.asList(new Thing(), new Thing(), new Thing()));
//        person.setName("Owning man");
//        person = dataAccess.save(person);
//        for (Thing thing : person.getThings()) {
//            thing.setOwner(person);
//            dataAccess.save(thing);
//        }
//        dataAccess.delete(person);
    }

}
