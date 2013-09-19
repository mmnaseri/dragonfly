package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.DataAccessObject;
import com.agileapes.dragonfly.entity.EntityContext;
import com.agileapes.dragonfly.sample.entities.LibraryCard;
import com.agileapes.dragonfly.sample.entities.Person;
import com.agileapes.dragonfly.sample.entities.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 23:06)
 */
@Service
public class CreatePersonService {

    @Autowired
    private EntityContext entityContext;

    @Autowired
    private DataAccess dataAccess;

    public void execute() {
        final Person person = entityContext.getInstance(Person.class);
        person.setLibraryCard(new LibraryCard());
        person.setName("Person - " + Math.abs((new Random().nextInt())));
        person.setBirthday(new Date());
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
