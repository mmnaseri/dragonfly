package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.sample.entities.Person;
import com.agileapes.dragonfly.sample.entities.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

/**
 * @author Mohammad Milad Naseri (m.m.naseri@gmail.com)
 * @since 1.0 (2013/9/13, 23:06)
 */
@Service
public class CreatePersonService {

    @Autowired
    private DataAccess dataAccess;

    public void execute() {
        final Person person = new Person();
        person.setName("Person - " + Math.abs((new Random().nextInt())));
        person.setBirthday(new Date());
        dataAccess.save(person);
        for (int i = 0; i < 3; i ++) {
            final Thing thing = new Thing();
            thing.setOwner(person);
            thing.setName("Thing - " + Math.abs((new Random().nextInt())));
            dataAccess.save(thing);
        }
    }

}
