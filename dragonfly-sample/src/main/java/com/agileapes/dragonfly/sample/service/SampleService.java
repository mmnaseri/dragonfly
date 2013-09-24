package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.sample.audit.Identifiable;
import com.agileapes.dragonfly.sample.entities.Group;
import com.agileapes.dragonfly.sample.entities.Person;
import com.agileapes.dragonfly.sample.entities.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/23, 12:55)
 */
@Service
public class SampleService {

    @Autowired
    private DataAccess dataAccess;

    public void execute() {
        final String[] names = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};
        for (String name : names) {
            final Group group = new Group();
            group.setName(name);
            dataAccess.save(group);
        }
        final List<Thing> things = dataAccess.findAll(Thing.class);
        for (Thing thing : things) {
            System.out.println(thing + ": " + ((Identifiable) thing).getUniqueKey());
            thing.setOwner(null);
        }
        final Person person = dataAccess.find(Person.class, 1L);
        for (Thing thing : person.getThings()) {
            System.out.println(thing + ": " + ((Identifiable) thing).getUniqueKey());
        }
        System.out.println((((Identifiable) person.getGroup()).getUniqueKey()) + " - " + person.getGroup().getName());
    }

}
