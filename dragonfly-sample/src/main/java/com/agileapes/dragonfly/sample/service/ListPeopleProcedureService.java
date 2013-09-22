package com.agileapes.dragonfly.sample.service;

import com.agileapes.dragonfly.data.DataAccess;
import com.agileapes.dragonfly.data.Reference;
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
